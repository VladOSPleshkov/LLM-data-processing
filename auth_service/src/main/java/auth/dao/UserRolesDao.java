package auth.dao;

import auth.entity.UserRoleId;
import auth.entity.UserRoleId_;
import auth.entity.UserRoles;
import auth.entity.UserRoles_;
import com.shared.dao.AbstractDao;
import jakarta.persistence.EntityGraph;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class UserRolesDao extends AbstractDao<UserRoles, UserRoleId> {

    public UserRolesDao() {
        super(UserRoles.class);
    }

    public List<UUID> findRoleIdsByUserId(UUID userId) {
        String jpql = "SELECT ur.id.roleId FROM UserRoles ur WHERE ur.id.userId = :userId";
        return entityManager.createQuery(jpql, UUID.class)
                .setParameter("userId", userId)
                .getResultList();
    }

    public List<UserRoles> findByUserIdWithDynamicEntityGraph(UUID userId) {
        EntityGraph<UserRoles> graph = entityManager.createEntityGraph(UserRoles.class);
        graph.addAttributeNodes(UserRoles_.user, UserRoles_.role);

        TypedQuery<UserRoles> query = entityManager.createQuery(
                "SELECT ur FROM UserRoles ur WHERE ur.id.userId = :userId", UserRoles.class);
        query.setParameter("userId", userId);
        query.setHint("jakarta.persistence.fetchgraph", graph);
        return query.getResultList();
    }

    public Optional<UserRoles> findByCompositeKey(UUID userId, UUID roleId) {
        String jpql = "SELECT ur FROM UserRoles ur WHERE ur.id.userId = :userId AND ur.id.roleId = :roleId";
        return entityManager.createQuery(jpql, UserRoles.class)
                .setParameter("userId", userId)
                .setParameter("roleId", roleId)
                .getResultStream()
                .findFirst();
    }

    public void unlink(UUID userId, UUID roleId) {
        deleteById(new UserRoleId(userId, roleId));
    }

    public List<UserRoles> findAllByUserId(UUID userId) {
        String jpql = "SELECT ur FROM UserRoles ur WHERE ur.id.userId = :userId";
        return entityManager.createQuery(jpql, UserRoles.class)
                .setParameter("userId", userId)
                .getResultList();
    }
    @Transactional
    public int deleteAllByUserId(UUID userId) {
        String jpql = "DELETE FROM UserRoles ur WHERE ur.id.userId = :userId";
        return entityManager.createQuery(jpql)
                .setParameter("userId", userId)
                .executeUpdate();
    }

    @Transactional
    public int deleteAllByRoleId(UUID roleId) {
        String jpql = "DELETE FROM UserRoles ur WHERE ur.id.roleId = :roleId";
        return entityManager.createQuery(jpql)
                .setParameter("roleId", roleId)
                .executeUpdate();
    }
}
