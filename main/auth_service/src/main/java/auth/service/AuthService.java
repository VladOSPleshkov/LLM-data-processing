package auth.service;

import auth.util.AuthTextProvider;
import contract.Repository;
import contract.Service;
import di.annotation.Autowire;
import di.annotation.Component;
import di.annotation.Qualifier;

@Component(name = "authService")
public class AuthService implements Service {

    private final Repository repository;

    // способ 1 — внедрение через конструктор с квалификатором,
    // так как интерфейс Repository реализуют три разных модуля
    @Autowire
    public AuthService(@Qualifier("authRepository") Repository repository) {
        this.repository = repository;
        System.out.println("  AuthService: внедрение через конструктор");
    }

    @Override
    public String execute() {
        return AuthTextProvider.serviceText(repository.execute());
    }
}