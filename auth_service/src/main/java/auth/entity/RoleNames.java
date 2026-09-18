package auth.entity;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.UUID;

@Getter
@RequiredArgsConstructor
public enum RoleNames {
    ADMIN("ROLE_ADMIN"),
    USER("ROLE_USER");

    @JsonValue
    private final String value;

    public Object getId() {
        return value;
    }
}