package review.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shared.CrudController;
import com.shared.dto.review.*;
import com.shared.service.IReviewService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import com.shared.dto.DeleteStatus;

import java.util.UUID;

@Component("reviewController")
public class ReviewController extends AbstractReviewController implements CrudController<CreateReviewRequest, ReviewFullResponse, UpdateReviewRequest, UUID> {

    private final IReviewService reviewService;

    public ReviewController(IReviewService reviewService, ObjectMapper objectMapper) {
        super(objectMapper, "reviewController");
        this.reviewService = reviewService;
    }

    @Override
    public String processCreate(CreateReviewRequest request) { return toJson(reviewService.createReview(request)); }

    @Override
    public String processGetById(UUID id) { return toJson(reviewService.getReviewById(id)); }

    @Override
    public String processGetAll() { return toJson(reviewService.getAllReviews()); }

    @Override
    public String processUpdate(UUID id, UpdateReviewRequest request) { return toJson(reviewService.updateReview(id, request)); }

    @Override
    public String processDelete(UUID id) {
        boolean deleted = reviewService.deleteReview(id);
        return toJson(new DeleteStatus(deleted, "Отзыв с ID " + id + " обработан"));
    }

    /** Доп. вариант использования — управление many-to-many Reviews<->Categories. */
    public String processAddCategory(AddCategoryToReviewRequest request) {
        return toJson(reviewService.addCategory(request));
    }

    public String processRemoveCategory(RemoveCategoryFromReviewRequest request) {
        return toJson(reviewService.removeCategory(request));
    }

    @Override
    public String execute() { return "reviewController активен"; }

}