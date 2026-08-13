package review.service;

import contract.Repository;
import contract.Service;
import di.annotation.Autowire;
import di.annotation.Component;
import di.annotation.Qualifier;
import review.util.ReviewTextProvider;

@Component(name = "reviewService")
public class ReviewService implements Service {

    // способ 2 — внедрение через поле
    @Autowire
    @Qualifier("reviewRepository")
    private Repository repository;

    @Override
    public String execute() {
        return ReviewTextProvider.serviceText(repository.execute());
    }
}