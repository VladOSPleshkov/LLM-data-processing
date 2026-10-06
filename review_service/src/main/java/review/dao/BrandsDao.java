package review.dao;

import com.shared.dao.AbstractDao;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;
import review.entity.Brands;

import java.util.Optional;
import java.util.UUID;

import static review.entity.Brands_.brandName;

@Repository
public class BrandsDao extends AbstractReviewDao<Brands, UUID> {

    public BrandsDao() {
        super(Brands.class);
    }
    @Override
    @PersistenceContext(unitName = "reviewUnit")
    public void setEntityManager(EntityManager entityManager) {
        super.setEntityManager(entityManager); }

    public Optional<Brands> findByName(String brandName) {
        TypedQuery<Brands> query = entityManager.createQuery(
                "SELECT b FROM Brands b WHERE b.brandName = :brandName", Brands.class);
        query.setParameter("brandName", brandName);
        return query.getResultStream().findFirst();
    }

    public long countReviewsByBrandId(UUID brandId) {
        TypedQuery<Long> query = entityManager.createQuery(
                "SELECT COUNT(r) FROM Reviews r WHERE r.brand.id = :brandId", Long.class);
        query.setParameter("brandId", brandId);
        return query.getSingleResult();
    }
}
