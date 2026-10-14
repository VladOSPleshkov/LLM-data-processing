package review.service;

import com.shared.dto.review.AnalysisReportDto;
import com.shared.exception.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import review.dao.AnalysisReportDao;
import review.entity.AnalysisReport;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AnalysisReportService {

    private final AnalysisReportDao analysisReportDao;

    @Transactional(value = "reviewTransactionManager", readOnly = true)
    public List<AnalysisReportDto> getByBrand(UUID brandId) {
        return analysisReportDao.findByBrandId(brandId).stream().map(this::toDto).toList();
    }

    @Transactional("reviewTransactionManager")
    public void acknowledge(UUID taskId) {
        AnalysisReport report = analysisReportDao.findById(taskId)
                .orElseThrow(() -> new EntityNotFoundException("Отчёт " + taskId + " не найден"));
        report.setAcknowledged(true);
    }

    private AnalysisReportDto toDto(AnalysisReport r) {
        return new AnalysisReportDto(
                r.getTaskId(),
                r.getBrand().getId(),
                r.getBrand().getBrandName(),
                r.getStatus(),
                r.getResult(),
                r.getErrorMessage(),
                r.getCompletedAt(),
                r.getReceivedAt(),
                r.isAcknowledged()
        );
    }
}
