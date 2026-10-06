package auth.dao;

import auth.entity.Roles;
import auth.entity.Roles_;
import auth.entity.UserRoles;
import auth.entity.UserRoles_;
import com.shared.dao.AbstractDao;
import jakarta.persistence.EntityGraph;
import jakarta.persistence.Subgraph;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class RolesDao extends AbstractDao<Roles, UUID> {

    public RolesDao() {
        super(Roles.class);
    }

    public Optional<Roles> findByIdWithUsersJoinFetch(UUID id) {
        String jpql = """
                SELECT DISTINCT r
                FROM Roles r
                LEFT JOIN FETCH r.userRoles ur
                LEFT JOIN FETCH ur.user
                WHERE r.id = :id
                """;
        TypedQuery<Roles> query = entityManager.createQuery(jpql, Roles.class);
        query.setParameter("id", id);
        return query.getResultStream().findFirst();
    }

    public Optional<Roles> findByIdWithNamedEntityGraph(UUID id) {
        EntityGraph<?> graph = entityManager.getEntityGraph("Roles.withUserRoles");

        TypedQuery<Roles> query = entityManager.createQuery(
                "SELECT r FROM Roles r WHERE r.id = :id", Roles.class);
        query.setParameter("id", id);
        query.setHint("jakarta.persistence.fetchgraph", graph);
        return query.getResultStream().findFirst();
    }

    public Optional<Roles> findByIdWithDynamicEntityGraph(UUID id) {
        EntityGraph<Roles> graph = entityManager.createEntityGraph(Roles.class);
        Subgraph<UserRoles> userRolesSubgraph = graph.addSubgraph(Roles_.userRoles.getName(), UserRoles.class);
        userRolesSubgraph.addAttributeNodes(UserRoles_.user);

        TypedQuery<Roles> query = entityManager.createQuery(
                "SELECT r FROM Roles r WHERE r.id = :id", Roles.class);
        query.setParameter("id", id);
        query.setHint("jakarta.persistence.fetchgraph", graph);
        return query.getResultStream().findFirst();
    }

    public Optional<Roles> findByName(String name) {
        String jpql = "SELECT r FROM Roles r WHERE r.name = :name";
        return entityManager.createQuery(jpql, Roles.class)
                .setParameter("name", name)
                .getResultStream()
                .findFirst();
    }

    public boolean existsByName(String name) {
        String jpql = "SELECT COUNT(r) FROM Roles r WHERE r.name = :name";
        Long count = entityManager.createQuery(jpql, Long.class)
                .setParameter("name", name)
                .getSingleResult();
        return count > 0;
    }
}