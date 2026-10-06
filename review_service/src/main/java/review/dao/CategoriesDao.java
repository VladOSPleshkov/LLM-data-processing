package review.dao;

import com.shared.dao.AbstractDao;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;
import review.entity.Categories;

import java.util.Optional;
import java.util.UUID;

@Repository
public class CategoriesDao extends AbstractReviewDao<Categories, UUID> {

    public CategoriesDao() {
        super(Categories.class);
    }
    @Override
    @PersistenceContext(unitName = "reviewUnit")
    public void setEntityManager(EntityManager entityManager) {
        super.setEntityManager(entityManager); }

    public Optional<Categories> findByName(String name) {
        TypedQuery<Categories> query = entityManager.createQuery(
                "SELECT c FROM Categories c WHERE c.name = :name", Categories.class);
        query.setParameter("name", name);
        return query.getResultStream().findFirst();
    }



    public long countReviewsByCategoryId(UUID categoryId) {
        TypedQuery<Long> query = entityManager.createQuery(
                "SELECT COUNT(r) FROM Reviews r JOIN r.categories c WHERE c.id = :categoryId", Long.class);
        query.setParameter("categoryId", categoryId);
        return query.getSingleResult();
    }
}
