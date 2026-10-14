package review;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shared.dto.analysis.ReviewAnalysisEvent;
import com.shared.dto.review.CreateReviewRequest;
import com.shared.security.config.SharedSecurityAutoConfiguration;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import review.config.SecurityConfig;
import review.dao.BrandsDao;
import review.entity.Brands;
import review.service.ReviewService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = {
        review.ReviewApplication.class,        // Стартовый класс самого сервиса отзывов
        SecurityConfig.class,                  // Конфиг безопасности тестируемого сервиса
        SharedSecurityAutoConfiguration.class  // Наша автоконфигурация валидатора из shared-api
})
@AutoConfigureMockMvc
@ActiveProfiles("test")
@EmbeddedKafka(
        partitions = 1,
        topics = {"review-processing-topic"},
        controlledShutdown = true
)
class SecurityAndKafkaJsonIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private EmbeddedKafkaBroker embeddedKafkaBroker;

    @MockitoBean
    private BrandsDao brandsDao; // Мокаем слой БД, чтобы тест не требовал реальный Postgres

    private Consumer<String, String> testConsumer;

    @BeforeEach
    void setUp() {
        // Настраиваем тестовый потребитель Kafka для чтения отправленных сервисом сообщений
        Map<String, Object> consumerProps = KafkaTestUtils.consumerProps("test-group", "true", embeddedKafkaBroker);
        consumerProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        consumerProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());

        ConsumerFactory<String, String> cf = new DefaultKafkaConsumerFactory<>(consumerProps);
        testConsumer = cf.createConsumer();
        embeddedKafkaBroker.consumeFromAllEmbeddedTopics(testConsumer);
    }

    @AfterEach
    void tearDown() {
        if (testConsumer != null) {
            testConsumer.close();
        }
    }
    @Test
    void createReview_WhenUnauthenticated_ShouldReturn401() throws Exception {
        CreateReviewRequest request = new CreateReviewRequest(
                UUID.randomUUID(), "Тестовый текст", 5, "Автор", "Тест", List.of("Категория")
        );

        mockMvc.perform(post("/api/v1/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    /**
     * Сценарий 2: Полноценный сквозной тест безопасности и брокера сообщений.
     * Симулируем прохождение валидного JWT токена с ролью ROLE_MANAGER.
     * Сервис должен:
     *  1. Успешно авторизовать запрос (200/201 OK).
     *  2. Отработать бизнес-логику.
     *  3. Отправить сериализованный JSON-ивент ReviewAnalysisEvent в EmbeddedKafka.
     */
    @Test
    @WithMockUser(username = "test_user", roles = {"MANAGER"})
    void createReview_WithValidToken_ShouldSaveAndPublishToKafka() throws Exception {
        UUID brandId = UUID.randomUUID();
        String brandName = "TestBrand";

        Brands mockBrand = Brands.builder()
                .id(brandId)
                .brandName(brandName)
                .createdAt(LocalDateTime.now())
                .build();

        // Задаем поведение мока БД
        when(brandsDao.findById(brandId)).thenReturn(Optional.of(mockBrand));

        CreateReviewRequest request = new CreateReviewRequest(
                brandId,
                "Отличный девайс, рекомендую к покупке!",
                5,
                "Алексей",
                "Тест",
                List.of("Электроника")
        );

        // Выполняем HTTP POST запрос (MockMvc симулирует прохождение через JwtAuthenticationFilter)
        mockMvc.perform(post("/api/v1/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated()/* или isOk() в зависимости от контроллера */);

        ConsumerRecord<String, String> singleRecord = KafkaTestUtils.getSingleRecord(
                testConsumer,
                "review-processing-topic",
                java.time.Duration.ofMillis(5000) // Передаем явный Duration вместо примитива
        );
        assertThat(singleRecord).isNotNull();
        assertThat(singleRecord.key()).isEqualTo(brandName);

        ReviewAnalysisEvent sentEvent = objectMapper.readValue(singleRecord.value(), ReviewAnalysisEvent.class);

        assertThat(sentEvent.text()).contains("Отличный девайс");
        assertThat(sentEvent.brandName()).isEqualTo(brandName);
        assertThat(sentEvent.categoryName()).isEqualTo("Электроника");
        assertThat(sentEvent.reviewId()).isNotNull();
    }
}