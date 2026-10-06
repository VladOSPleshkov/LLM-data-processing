package review.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shared.CrudController;
import com.shared.dto.review.*;
import com.shared.service.IReviewService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import com.shared.dto.DeleteStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/reviews")
public class ReviewController  {

    private final IReviewService reviewService;

    public ReviewController(IReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @GetMapping
    public ResponseEntity<List<ReviewFullResponse>> getAllReviews() {
        return ResponseEntity.ok(reviewService.getAllReviews());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReviewFullResponse> getReviewById(@PathVariable UUID id) {
        return ResponseEntity.ok(reviewService.getReviewById(id));
    }

    @PostMapping
    public ResponseEntity<ReviewFullResponse> createReview(@RequestBody CreateReviewRequest request) {
        ReviewFullResponse created = reviewService.createReview(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReviewFullResponse> updateReview(@PathVariable UUID id, @RequestBody UpdateReviewRequest request) {
        return ResponseEntity.ok(reviewService.updateReview(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReview(@PathVariable UUID id) {
        reviewService.deleteReview(id);
        return ResponseEntity.noContent().build();
    }

    // Привязка категории к отзыву (связь ресурса)
    @PostMapping("/{id}/categories")
    public ResponseEntity<ReviewFullResponse> addCategoryToReview(
            //@PathVariable UUID id,
            @RequestBody AddCategoryToReviewRequest request) {
        return ResponseEntity.ok(reviewService.addCategory(request));
    }
}