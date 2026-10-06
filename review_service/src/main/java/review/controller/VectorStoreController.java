package review.controller;

import com.shared.dto.review.CreateVectorRequest;
import com.shared.dto.review.UpdateVectorRequest;
import com.shared.dto.review.VectorStoreDto;
import com.shared.service.IVectorStoreService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/vectors")
public class VectorStoreController {

    private final IVectorStoreService vectorStoreService;

    public VectorStoreController(IVectorStoreService vectorStoreService) {
        this.vectorStoreService = vectorStoreService;
    }

//    @GetMapping
//    public ResponseEntity<List<VectorStoreDto>> getAllVectors() {
//        return ResponseEntity.ok(vectorStoreService.getAllVectors());
//    }

    @GetMapping("/{id}")
    public ResponseEntity<VectorStoreDto> getVectorById(@PathVariable UUID id) {
        return ResponseEntity.ok(vectorStoreService.getVectorById(id));
    }

    @PostMapping
    public ResponseEntity<VectorStoreDto> createVector(@RequestBody CreateVectorRequest request) {
        VectorStoreDto created = vectorStoreService.createVector(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<VectorStoreDto> updateVector(@PathVariable UUID id, @RequestBody UpdateVectorRequest request) {
        return ResponseEntity.ok(vectorStoreService.updateVector(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVector(@PathVariable UUID id) {
        vectorStoreService.deleteVector(id);
        return ResponseEntity.noContent().build();
    }
}
