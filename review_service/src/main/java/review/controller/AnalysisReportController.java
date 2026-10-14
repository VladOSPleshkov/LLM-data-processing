package review.controller;

import com.shared.dto.review.AnalysisReportDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import review.service.AnalysisReportService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
public class AnalysisReportController {

    private final AnalysisReportService analysisReportService;

    @GetMapping("/brand/{brandId}")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<List<AnalysisReportDto>> getByBrand(@PathVariable UUID brandId) {
        return ResponseEntity.ok(analysisReportService.getByBrand(brandId));
    }

    @PostMapping("/{taskId}/acknowledge")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<Void> acknowledge(@PathVariable UUID taskId) {
        analysisReportService.acknowledge(taskId);
        return ResponseEntity.noContent().build();
    }
}