package auth.controller;

import auth.util.AuthTextProvider;
import contract.Controller;
import contract.Service;
import di.annotation.Autowire;
import di.annotation.Component;
import di.annotation.Qualifier;
import di.annotation.Value;

@Component(name = "authController")
public class AuthController implements Controller {

    private final Service service;

    @Value(key = "AUTH_MODULE_NAME")
    private String moduleName;

    @Autowire
    public AuthController(@Qualifier("authService") Service service) {
        this.service = service;
        System.out.println("  AuthController: внедрение через конструктор");
    }

    @Override
    public String execute() {
        return AuthTextProvider.controllerText(service.execute());
    }

    @Override
    public String getModuleName() {
        return moduleName;
    }
}