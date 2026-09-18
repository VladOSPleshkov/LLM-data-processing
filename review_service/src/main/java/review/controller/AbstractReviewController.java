package review.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;

public class AbstractReviewController {
    protected ObjectMapper objectMapper;
    private final String controllerName;

    @Getter
    @Value("${REVIEW_MODULE_NAME:review_service}")
    private String moduleName;

    public AbstractReviewController(ObjectMapper objectMapper, String controllerName) {
        this.objectMapper = new ObjectMapper();
        this.controllerName = controllerName;
    }
    public String execute() {
        return controllerName + " активен";
    }

    protected String toJson(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка Jackson-сериализации в " + controllerName, e);
        }
    }
}
