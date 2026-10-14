package review.dao;

import com.shared.dao.AbstractDao;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;
import review.entity.AnalysisReport;

import java.util.List;
import java.util.UUID;


@Repository
public class AnalysisReportDao extends AbstractDao<AnalysisReport, UUID> {

    public AnalysisReportDao() {
        super(AnalysisReport.class);
    }

    public List<AnalysisReport> findByBrandId(UUID brandId) {
        TypedQuery<AnalysisReport> query = entityManager.createQuery(
                "SELECT r FROM AnalysisReport r WHERE r.brandId = :brandId ORDER BY r.receivedAt DESC",
                AnalysisReport.class);
        query.setParameter("brandId", brandId);
        return query.getResultList();
    }
}