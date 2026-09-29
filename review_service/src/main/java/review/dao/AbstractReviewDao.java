package review.dao;

import com.shared.dao.AbstractDao;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.io.Serializable;

public abstract class AbstractReviewDao<T, ID extends Serializable> extends AbstractDao<T, ID> {

    protected AbstractReviewDao(Class<T> entityClass) {
        super(entityClass);
    }

    /**
     * Переопределяем внедрение EntityManager с указанием reviewUnit.
     * Spring применит эту аннотацию ко всем DAO, наследующим AbstractReviewDao.
     */
    @Override
    @PersistenceContext(unitName = "reviewUnit")
    public void setEntityManager(EntityManager entityManager) {
        super.setEntityManager(entityManager);
    }
}
