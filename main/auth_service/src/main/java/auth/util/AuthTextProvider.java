package auth.util;

public final class AuthTextProvider {

    private AuthTextProvider() {}

    public static String repositoryText() {
        return "Пользователи и роли загружены из auth_db";
    }

    public static String serviceText(String data) {
        return "AuthService обработал: [" + data + "]";
    }

    public static String controllerText(String data) {
        return "AuthController: " + data;
    }
}