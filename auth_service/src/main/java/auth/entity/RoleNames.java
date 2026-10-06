package auth.entity;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum RoleNames {
    ROLE_ADMIN("ROLE_ADMIN"),
    ROLE_MANAGER("ROLE_MANAGER");

    @JsonValue
    private final String value;

    public Object getId() {
        return value;
    }

    public static RoleNames fromValue(String text) {
        if (text == null) return null;
        return Arrays.stream(values())
                .filter(r -> r.name().equalsIgnoreCase(text) || r.getValue().equalsIgnoreCase(text))
        .findFirst().orElseThrow(() -> new IllegalArgumentException("Неизвестная роль: " + text)); }
}