package auth.dao;

import auth.entity.User;
import auth.entity.UserRoles;
import auth.entity.User_;
import auth.entity.UserRoles_;
import com.shared.dao.AbstractDao;
import jakarta.persistence.EntityGraph;
import jakarta.persistence.Subgraph;
import jakarta.persistence.TypedQuery;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * DAO пользователя. Демонстрирует три способа загрузки User + userRoles + role
 * за ОДИН SQL-запрос, устраняя проблему N+1.
 */
@Repository
public class UserDao extends AbstractDao<User, UUID> {

    public UserDao() {
        super(User.class);
    }

    /**
     * Способ 1: явный JPQL LEFT JOIN FETCH.
     */
    public Optional<User> findByIdWithRolesJoinFetch(UUID id) {
        String jpql = """
                SELECT DISTINCT u
                FROM User u
                LEFT JOIN FETCH u.userRoles ur
                LEFT JOIN FETCH ur.role
                WHERE u.id = :id
                """;
        TypedQuery<User> query = entityManager.createQuery(jpql, User.class);
        query.setParameter("id", id);
        return query.getResultStream().findFirst();
    }

    /**
     * Способ 2: статический @NamedEntityGraph "User.withRoles",
     * объявленный прямо на сущности User, подмешивается через fetchgraph-хинт.
     */
    public Optional<User> findByIdWithNamedEntityGraph(UUID id) {
        EntityGraph<?> graph = entityManager.getEntityGraph("User.withRoles");

        TypedQuery<User> query = entityManager.createQuery(
                "SELECT u FROM User u WHERE u.id = :id", User.class);
        query.setParameter("id", id);
        query.setHint("jakarta.persistence.fetchgraph", graph);
        return query.getResultStream().findFirst();
    }

    /**
     * Способ 3: динамический EntityGraph, построенный в Java-коде
     * с использованием атрибутов метамодели User_ / UserRoles_.
     */
    public Optional<User> findByIdWithDynamicEntityGraph(UUID id) {
        EntityGraph<User> graph = entityManager.createEntityGraph(User.class);
        Subgraph<UserRoles> userRolesSubgraph = graph.addSubgraph(User_.userRoles.getName(), UserRoles.class);
        userRolesSubgraph.addAttributeNodes(UserRoles_.role);

        TypedQuery<User> query = entityManager.createQuery(
                "SELECT u FROM User u WHERE u.id = :id", User.class);
        query.setParameter("id", id);
        query.setHint("jakarta.persistence.fetchgraph", graph);
        return query.getResultStream().findFirst();
    }

    public Optional<User> findByUsername(String username) {
        String jpql = "SELECT u FROM User u WHERE u.username = :username";
        return entityManager.createQuery(jpql, User.class)
                .setParameter("username", username)
                .getResultStream()
                .findFirst();
    }

    public Optional<User> findByEmail(String email) {
        String jpql = "SELECT u FROM User u WHERE u.email = :email";
        return entityManager.createQuery(jpql, User.class)
                .setParameter("email", email)
                .getResultStream()
                .findFirst();
    }

    /** Массовая выборка со снятием N+1 через statically named graph. */
    public List<User> findAllWithRoles() {
        EntityGraph<?> graph = entityManager.getEntityGraph("User.withRoles");
        return entityManager.createQuery("SELECT DISTINCT u FROM User u", User.class)
                .setHint("jakarta.persistence.fetchgraph", graph)
                .getResultList();
    }
}