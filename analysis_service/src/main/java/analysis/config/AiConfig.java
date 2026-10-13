package analysis.config;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.openai.OpenAiEmbeddingModel;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AiConfig {
    @Bean
    public EmbeddingModel embeddingModel(
            @Value("${spring.ai.openai.api-key}") String apiKey,
            @Value("${spring.ai.openai.base-url}") String baseUrl) {
        // Используем тот же эндпоинт для генерации векторов (например, через OpenRouter/Groq)
        var openAiApi = new OpenAiApi(baseUrl, apiKey);
        return new OpenAiEmbeddingModel(openAiApi);
    }
}
