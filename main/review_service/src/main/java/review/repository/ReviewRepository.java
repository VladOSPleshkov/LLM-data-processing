package review.repository;

import contract.Repository;
import di.annotation.Component;
import di.annotation.Value;
import review.util.ReviewTextProvider;

@Component(name = "reviewRepository")
public class ReviewRepository implements Repository {

    @Value(key = "REVIEW_DB_URL")
    private String dbUrl;

    @Value(key = "REVIEW_DB_USER")
    private String dbUser;

    @Override
    public String execute() {
        System.out.println("  ReviewRepository: " + dbUrl + " user=" + dbUser);
        return ReviewTextProvider.repositoryText();
    }
}