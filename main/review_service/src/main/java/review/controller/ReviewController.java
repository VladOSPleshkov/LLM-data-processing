package review.controller;

import contract.Controller;
import contract.Service;
import di.annotation.Autowire;
import di.annotation.Component;
import di.annotation.Qualifier;
import di.annotation.Value;
import review.util.ReviewTextProvider;

@Component(name = "reviewController")
public class ReviewController implements Controller {

    @Autowire
    @Qualifier("reviewService")
    private Service service;

    @Value(key = "REVIEW_MODULE_NAME")
    private String moduleName;

    @Override
    public String execute() {
        return ReviewTextProvider.controllerText(service.execute());
    }

    @Override
    public String getModuleName() {
        return moduleName;
    }
}