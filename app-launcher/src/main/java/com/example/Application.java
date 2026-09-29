package com.example;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import com.shared.dto.auth.AssignRoleRequest;
import com.shared.dto.auth.CreateBlacklistTokenRequest;
import com.shared.dto.auth.CreateRoleRequest;
import com.shared.dto.auth.CreateUserRequest;
import com.shared.dto.auth.RevokeRoleRequest;
import com.shared.dto.auth.UpdateBlacklistTokenRequest;
import com.shared.dto.auth.UpdateRoleRequest;
import com.shared.dto.auth.UpdateUserRequest;
import com.shared.dto.analysis.UpdateTaskRequest;
import com.shared.dto.review.CreateReviewRequest;
import com.shared.dto.review.CreateBrandRequest;
import com.shared.dto.review.UpdateBrandRequest;
import com.shared.dto.review.UpdateReviewRequest;
import com.shared.dto.review.CreateCategoryRequest;
import com.shared.dto.review.UpdateCategoryRequest;
import com.shared.dto.analysis.CreateTaskRequest;
import com.shared.dto.review.RemoveCategoryFromReviewRequest;
import com.shared.dto.review.CreateVectorRequest;
import com.shared.dto.review.AddCategoryToReviewRequest;
import com.shared.dto.review.UpdateVectorRequest;
import com.shared.dto.analysis.CreateTokenUsageRequest;
import com.shared.dto.analysis.UpdateTokenUsageRequest;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;


import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import liquibase.integration.spring.SpringLiquibase;

import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import review.controller.BrandsController;
import review.controller.CategoriesController;
import review.controller.ReviewController;
import review.controller.VectorStoreController;
import analysis.controller.TasksController;
//import analysis.controller.TokenUsageController;
import auth.controller.RolesController;
import auth.controller.TokenBlacklistController;
import auth.controller.UserController;

import javax.sql.DataSource;


@Configuration
@ComponentScan({"auth", "analysis", "review", "com.shared"})
@EnableTransactionManagement
@PropertySources({
        @PropertySource(value = "classpath:auth_app.properties", ignoreResourceNotFound = true),
        @PropertySource(value = "classpath:analysis_app.properties", ignoreResourceNotFound = true),
        @PropertySource(value = "classpath:review_app.properties", ignoreResourceNotFound = true)
})
public class Application {

    private static final String DRIVER = "org.postgresql.Driver";

//auth
@Value("${auth.db.url:jdbc:postgresql://localhost:5433/auth_db}")
private String authUrl;

    @Value("${auth.db.username:auth_user}")
    private String authUser;

    @Value("${auth.db.password:passw}")
    private String authPassword;

    @Value("${auth.liquibase.changelog:classpath:changelogs/homework-2/auth_service/changelog-auth.xml}")
    private String authChangelog;

    @Primary // Делаем основным по умолчанию
    @Bean(name = "authDataSource")
    public DataSource authDataSource() {
        return createDataSource(authUrl, authUser, authPassword);
    }

    @Bean
    public SpringLiquibase liquibaseAuth(@Qualifier("authDataSource") DataSource ds) {
        return liquibase(ds, authChangelog);
    }
    @Primary
    @Bean(name = "entityManagerFactory") // Имя по умолчанию для @PersistenceContext
    public LocalContainerEntityManagerFactoryBean authEntityManagerFactory(
            @Qualifier("authDataSource") DataSource dataSource) {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(dataSource);
        em.setPackagesToScan("auth.entity", "com.shared.entity");
        em.setPersistenceUnitName("authUnit");

        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);
        em.setJpaProperties(hibernateProperties());
        return em;
    }
    @Primary
    @Bean(name = "transactionManager") // Менеджер транзакций по умолчанию
    public PlatformTransactionManager authTransactionManager(
            @Qualifier("entityManagerFactory") LocalContainerEntityManagerFactoryBean emFactory) {
        return new JpaTransactionManager(emFactory.getObject());
    }
//analysis
@Value("${analysis.db.url:jdbc:postgresql://localhost:5434/analysis_db}")
private String analysisUrl;

    @Value("${analysis.db.username:analysis_user}")
    private String analysisUser;

    @Value("${analysis.db.password:passw}")
    private String analysisPassword;

    @Value("${analysis.liquibase.changelog:classpath:changelogs/homework-2/analysis_service/changelog-analysis.xml}")
    private String analysisChangelog;

    @Bean(name = "analysisDataSource")
    public DataSource analysisDataSource() {
        return createDataSource(analysisUrl, analysisUser, analysisPassword);
    }

    @Bean
    public SpringLiquibase liquibaseAnalysis(@Qualifier("analysisDataSource") DataSource ds) {
        return liquibase(ds, analysisChangelog);
    }

    @Bean(name = "analysisEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean analysisEntityManagerFactory(
            @Qualifier("analysisDataSource") DataSource dataSource) {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(dataSource);
        em.setPackagesToScan("analysis.entity");
        em.setPersistenceUnitName("analysisUnit");

        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);
        em.setJpaProperties(hibernateProperties());
        return em;
    }
    @Bean(name = "analysisTransactionManager")
    public PlatformTransactionManager analysisTransactionManager(
            @Qualifier("analysisEntityManagerFactory") LocalContainerEntityManagerFactoryBean emFactory) {
        return new JpaTransactionManager(emFactory.getObject());
    }

    //review
    @Value("${review.db.url:jdbc:postgresql://localhost:5435/review_db}")
    private String reviewUrl;

    @Value("${review.db.username:review_user}")
    private String reviewUser;

    @Value("${review.db.password:passw}")
    private String reviewPassword;

    @Value("${review.liquibase.changelog:classpath:changelogs/homework-2/review_service/changelog-review.xml}")
    private String reviewChangelog;

    @Bean(name = "reviewDataSource")
    public DataSource reviewDataSource() {
        return createDataSource(reviewUrl, reviewUser, reviewPassword);
    }

    @Bean
    public SpringLiquibase liquibaseReview(@Qualifier("reviewDataSource") DataSource ds) {
        return liquibase(ds, reviewChangelog);
    }

    @Bean(name = "reviewEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean reviewEntityManagerFactory(
            @Qualifier("reviewDataSource") DataSource dataSource) {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(dataSource);
        em.setPackagesToScan("review.entity");
        em.setPersistenceUnitName("reviewUnit");

        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);
        em.setJpaProperties(hibernateProperties());
        return em;
    }
    @Bean(name = "reviewTransactionManager")
    public PlatformTransactionManager reviewTransactionManager(
            @Qualifier("reviewEntityManagerFactory") LocalContainerEntityManagerFactoryBean emFactory) {
        return new JpaTransactionManager(emFactory.getObject());
    }

//общие бины и доп методы
    private DataSource createDataSource(String url, String user, String password) {
        DriverManagerDataSource ds = new DriverManagerDataSource();
        ds.setDriverClassName(DRIVER);
        ds.setUrl(url);
        ds.setUsername(user);
        ds.setPassword(password);
        return ds;
    }

    @Bean
    public SpringLiquibase liquibase(DataSource dataSource, String changelog) {
        SpringLiquibase liquibase = new SpringLiquibase();
        liquibase.setDataSource(dataSource);
        liquibase.setChangeLog(changelog);
        return liquibase;
    }
    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return mapper;
    }
    private Properties hibernateProperties() {
        Properties properties = new Properties();
        properties.setProperty("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
        properties.setProperty("hibernate.show_sql", "true");
        properties.setProperty("hibernate.format_sql", "true");
        properties.setProperty("hibernate.hbm2ddl.auto", "none");
        return properties;
    }
    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }
    private static UUID extractUuidId(ObjectMapper mapper, String json) throws Exception {
        JsonNode node = mapper.readTree(json);
        return UUID.fromString(node.get("id").asText());
    }
    private static UUID getOrCreateRoleId(RolesController rolesController, ObjectMapper mapper, String roleName, String description) throws Exception {
        String allRolesJson = rolesController.processGetAll();
        JsonNode rolesArray = mapper.readTree(allRolesJson);

        for (JsonNode roleNode : rolesArray) {
            if (roleName.equalsIgnoreCase(roleNode.get("name").asText())) {
                System.out.println("--- FOUND existing role (" + roleName + "): " + roleNode.get("id").asText());
                return UUID.fromString(roleNode.get("id").asText());
            }
        }
        throw new IllegalStateException("Роль '" + roleName + "' не найдена в БД. Проверьте выполнение файлов миграции.");
    }
    @SuppressWarnings("unchecked")
    public static void main(String[] args) throws Exception {

        var context = new AnnotationConfigApplicationContext(Application.class);

        ObjectMapper mapper = context.getBean(ObjectMapper.class);

        /// auth_service

        RolesController rolesController = context.getBean("rolesController", RolesController.class);
        UserController userController = context.getBean("userController", UserController.class);
        TokenBlacklistController tokenController =
                context.getBean("tokenBlacklistController", TokenBlacklistController.class);

        System.out.println("\nroles");
        UUID role1Id = getOrCreateRoleId(rolesController, mapper, "ROLE_ADMIN", "Полный доступ к системе");
        UUID role2Id = getOrCreateRoleId(rolesController, mapper, "ROLE_MANAGER", "..");

        System.out.println("--- READ role #1 ---");
        System.out.println(rolesController.processGetById(role1Id));

        System.out.println("--- DELETE role #2 ---");
        System.out.println(rolesController.processDelete(role2Id));

        System.out.println("--- UPDATE role #1 ---");
        System.out.println(rolesController.processUpdate(role1Id,
                new UpdateRoleRequest("Обновлённое описание роли ADMIN")));

        System.out.println("--- READ role #1 again ---");
        System.out.println(rolesController.processGetById(role1Id));

        System.out.println("\n=users");

        List<String> adminRoles = List.of("ROLE_ADMIN");
        List<String> noRoles = List.of("ROLE_MANAGER");
        String user1Json = userController.processCreate(
                new CreateUserRequest("alice", "keycloak-sub-alice", "alice@example.com", adminRoles));
        System.out.println(user1Json);
        //ConcurrencyTestRunner.runUsersConcurrencyTest(userController, 10);
        UUID user1Id = extractUuidId(mapper, user1Json);

        String user2Json = userController.processCreate(
                new CreateUserRequest("bob", "keycloak-sub-bob", "bob@example.com", noRoles));
        System.out.println(user2Json);
        UUID user2Id = extractUuidId(mapper, user2Json);

        System.out.println(userController.processGetById(user1Id));
        System.out.println(userController.processDelete(user2Id));
//        System.out.println(userController.processUpdate(user1Id,
//                new UpdateUserRequest("alice.new@example.com", adminRoles)));
        System.out.println(userController.processGetById(user1Id));

        System.out.println(userController.processAssignRole(
                new AssignRoleRequest(user1Id, role1Id, user1Id)));
        System.out.println(userController.processRevokeRole(
                new RevokeRoleRequest(user1Id, role1Id)));

        System.out.println("\nrefreshtokenblacklist");
//        String token1Json = tokenController.processCreate(new CreateBlacklistTokenRequest(
//                "refresh_token_abc123",LocalDateTime.now().plusDays(7), user1Id, LocalDateTime.now().plusDays(7)));
//        System.out.println("--- CREATE blacklist entry #1 ---\n" + token1Json);
//        UUID token1Id = UUID.fromString(mapper.readTree(token1Json).get("id").asText());
//
//        String token2Json = tokenController.processCreate(new CreateBlacklistTokenRequest(
//                "refresh_token_def456", user1Id, LocalDateTime.now().plusDays(3)));
//        System.out.println("--- CREATE blacklist entry #2 ---\n" + token2Json);
//        UUID token2Id = UUID.fromString(mapper.readTree(token2Json).get("id").asText());
//
//        System.out.println("--- READ blacklist entry #1 ---");
//        System.out.println(tokenController.processGetById(token1Id));
//
//        System.out.println("--- DELETE blacklist entry #2 ---");
//        System.out.println(tokenController.processDelete(token2Id));
//
//        System.out.println("--- UPDATE blacklist entry #1 ---");
//        System.out.println(tokenController.processUpdate(token1Id,
//                new UpdateBlacklistTokenRequest(LocalDateTime.now().plusDays(30))));
//
//        System.out.println("--- READ blacklist entry #1 again ---");
//        System.out.println(tokenController.processGetById(token1Id));

        /// review_service

        BrandsController brandsController = context.getBean("brandsController", BrandsController.class);
    CategoriesController categoriesController = context.getBean("categoriesController", CategoriesController.class);
    ReviewController reviewController = context.getBean("reviewController", ReviewController.class);
    VectorStoreController vectorStoreController = context.getBean("vectorStoreController", VectorStoreController.class);

        System.out.println("\nbrands");
    String brand1Json = brandsController.processCreate(new CreateBrandRequest("Nike", "Спортивная одежда"));
        System.out.println(brand1Json);
    UUID brand1Id = extractUuidId(mapper, brand1Json);

    String brand2Json = brandsController.processCreate(new CreateBrandRequest("Adidas", "Спортивная обувь"));
        System.out.println(brand2Json);
    UUID brand2Id = extractUuidId(mapper, brand2Json);

        System.out.println(brandsController.processGetById(brand1Id));
        System.out.println(brandsController.processDelete(brand2Id));
        System.out.println(brandsController.processUpdate(brand1Id, new UpdateBrandRequest("Обновлённое описание Nike")));
        System.out.println(brandsController.processGetById(brand1Id));

        System.out.println("\ncategories");
    String cat1Json = categoriesController.processCreate(new CreateCategoryRequest("Обувь"));
        System.out.println(cat1Json);
    UUID cat1Id = extractUuidId(mapper, cat1Json);

    String cat2Json = categoriesController.processCreate(new CreateCategoryRequest("Одежда"));
        System.out.println(cat2Json);
    UUID cat2Id = extractUuidId(mapper, cat2Json);

        System.out.println(categoriesController.processGetById(cat1Id));
        System.out.println(categoriesController.processDelete(cat2Id));
        System.out.println(categoriesController.processUpdate(cat1Id, new UpdateCategoryRequest("Спортивная обувь")));
        System.out.println(categoriesController.processGetById(cat1Id));

        System.out.println("\nreviews");
    String review1Json = reviewController.processCreate(new CreateReviewRequest(
            brand1Id, "Отличные кроссовки, очень удобные", 5, "Иван",
            "site", List.of("Обувь"), new float[]{0.12f, -0.45f, 0.78f}, "source=web"));
        System.out.println(review1Json);
    UUID review1Id = extractUuidId(mapper, review1Json);

    String review2Json = reviewController.processCreate(new CreateReviewRequest(
            brand1Id, "Размер маломерит", 3, "Пётр",
            "app", List.of("Обувь"), new float[]{0.05f, 0.11f, -0.32f}, "source=app"));
        System.out.println(review2Json);
    UUID review2Id = extractUuidId(mapper, review2Json);

        System.out.println(reviewController.processGetById(review1Id));
        System.out.println(reviewController.processDelete(review2Id));
        System.out.println(reviewController.processUpdate(review1Id,
            new UpdateReviewRequest("Отличные кроссовки, покупаю второй раз", 5, List.of("Обувь", "Спортивная обувь"))));
        System.out.println(reviewController.processGetById(review1Id));

        System.out.println("--- Доп. вариант использования: удаление категории у отзыва ---");
        System.out.println(reviewController.processRemoveCategory(new RemoveCategoryFromReviewRequest(review1Id, cat1Id)));

        System.out.println("--- Доп. вариант использования: повторное добавление категории ---");
        System.out.println(reviewController.processAddCategory(new AddCategoryToReviewRequest(review1Id, cat1Id)));

        System.out.println("\n================ VECTOR STORE ================");
    String vector1Json = vectorStoreController.processCreate(
            new CreateVectorRequest(review1Id, new float[]{0.9f, 0.1f}, "manual-reindex"));
        System.out.println(vector1Json);
    UUID vector1Id = extractUuidId(mapper, vector1Json);

    String vector2Json = vectorStoreController.processCreate(
            new CreateVectorRequest(review1Id, new float[]{0.2f, 0.7f}, "manual-reindex-2"));
        System.out.println(vector2Json);
    UUID vector2Id = extractUuidId(mapper, vector2Json);

        System.out.println(vectorStoreController.processGetById(vector1Id));
        System.out.println(vectorStoreController.processDelete(vector2Id));
        System.out.println(vectorStoreController.processUpdate(vector1Id,
            new UpdateVectorRequest(new float[]{0.99f, 0.01f}, "reindexed-final")));
        System.out.println(vectorStoreController.processGetById(vector1Id));

        /// analysis_service

        TasksController tasksController = context.getBean("tasksController", TasksController.class);

        System.out.println("\ntasks");
        String task1Json = tasksController.processCreate(
                new CreateTaskRequest(UUID.randomUUID(), "Nike", "Проанализируй отзывы за последний месяц", 5));
        System.out.println("--- CREATE task #1 ---\n" + task1Json);
        UUID task1Id = UUID.fromString(mapper.readTree(task1Json).get("id").asText());

        String task2Json = tasksController.processCreate(
                new CreateTaskRequest(UUID.randomUUID(), "Adidas", "Оцени тональность новых отзывов", 10));
        System.out.println("--- CREATE task #2 ---\n" + task2Json);
        UUID task2Id = UUID.fromString(mapper.readTree(task2Json).get("id").asText());

        System.out.println("--- READ task #1 ---");
        System.out.println(tasksController.processGetById(task1Id));

        System.out.println("--- DELETE task #2 ---");
        System.out.println(tasksController.processDelete(task2Id));

        System.out.println("--- UPDATE task #1 ---");
        System.out.println(tasksController.processUpdate(task1Id,
                new UpdateTaskRequest("COMPLETED", "85% отзывов позитивны", null, 1845)));

        System.out.println("--- READ task #1 again ---");
        System.out.println(tasksController.processGetById(task1Id));
        //ConcurrencyTestRunner.runTasksConcurrencyTest(tasksController, 10);

        //System.out.println("--- Полный RAG-конвейер (доп. вариант использования сервиса) ---");
        //System.out.println(tasksController.processStartAnalysis(
                //new CreateTaskRequest(1L, "Nike", "Полный цикл анализа отзывов", 5)));

        context.close();
    }
}