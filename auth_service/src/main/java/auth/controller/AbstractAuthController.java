package auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;

public class AbstractAuthController {
    protected final ObjectMapper objectMapper;
    private final String controllerName;

    @Getter
    @Value("${AUTH_MODULE_NAME:auth-service}")
    private String moduleName;

    public AbstractAuthController(ObjectMapper objectMapper, String controllerName) {
        this.objectMapper = objectMapper;
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
