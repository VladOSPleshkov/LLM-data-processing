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

@Repository
public class UserDao extends AbstractDao<User, UUID> {

    public UserDao() {
        super(User.class);
    }

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
        return query.getResultList().stream().findFirst();
    }

    public Optional<User> findByIdWithNamedEntityGraph(UUID id) {
        EntityGraph<?> graph = entityManager.getEntityGraph("User.withRoles");

        TypedQuery<User> query = entityManager.createQuery(
                "SELECT u FROM User u WHERE u.id = :id", User.class);
        query.setParameter("id", id);
        query.setHint("jakarta.persistence.fetchgraph", graph);
        return query.getResultList().stream().findFirst();
    }

    public Optional<User> findByIdWithDynamicEntityGraph(UUID id) {
        EntityGraph<User> graph = entityManager.createEntityGraph(User.class);
        Subgraph<UserRoles> userRolesSubgraph = graph.addSubgraph(User_.userRoles.getName(), UserRoles.class);
        userRolesSubgraph.addAttributeNodes(UserRoles_.role);

        TypedQuery<User> query = entityManager.createQuery(
                "SELECT u FROM User u WHERE u.id = :id", User.class);
        query.setParameter("id", id);
        query.setHint("jakarta.persistence.fetchgraph", graph);
        return query.getResultList().stream().findFirst();
    }

    public Optional<User> findByUsername(String username) {
        String jpql = "SELECT u FROM User u WHERE u.username = :username";
        return entityManager.createQuery(jpql, User.class)
                .setParameter("username", username)
                .getResultList()
                .stream()
                .findFirst();
    }

    public Optional<User> findByEmail(String email) {
        String jpql = "SELECT u FROM User u WHERE u.email = :email";
        return entityManager.createQuery(jpql, User.class)
                .setParameter("email", email)
                .getResultList()
                .stream()
                .findFirst(); // Заменено с getResultStream()
    }

    public List<User> findAllWithRoles() {
        EntityGraph<?> graph = entityManager.getEntityGraph("User.withRoles");
        return entityManager.createQuery("SELECT DISTINCT u FROM User u", User.class)
                .setHint("jakarta.persistence.fetchgraph", graph)
                .getResultList();
    }
}