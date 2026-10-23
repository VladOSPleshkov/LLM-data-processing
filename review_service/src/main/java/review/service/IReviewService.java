package review.service;

import com.shared.dto.review.AddCategoryToReviewRequest;
import com.shared.dto.review.CreateReviewRequest;
import com.shared.dto.review.RemoveCategoryFromReviewRequest;
import com.shared.dto.review.ReviewFullResponse;
import com.shared.dto.review.UpdateReviewRequest;

import java.util.List;
import java.util.UUID;

public interface IReviewService {
    ReviewFullResponse createReview(CreateReviewRequest request);
    ReviewFullResponse getReviewById(UUID id);
    List<ReviewFullResponse> getAllReviews();
    ReviewFullResponse updateReview(UUID id, UpdateReviewRequest request);
    boolean deleteReview(UUID id);

    ReviewFullResponse addCategory(AddCategoryToReviewRequest request);
    ReviewFullResponse removeCategory(RemoveCategoryFromReviewRequest request);
}