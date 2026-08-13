package auth.repository;

import auth.util.AuthTextProvider;
import contract.Repository;
import di.annotation.Component;
import di.annotation.Value;

@Component(name = "authRepository")
public class AuthRepository implements Repository {

    @Value(key = "AUTH_DB_URL")
    private String dbUrl;

    @Value(key = "AUTH_DB_USER")
    private String dbUser;

    @Override
    public String execute() {
        System.out.println("  AuthRepository: " + dbUrl + " user=" + dbUser);
        return AuthTextProvider.repositoryText();
    }
}