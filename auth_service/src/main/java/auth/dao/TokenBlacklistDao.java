package auth.dao;

import auth.entity.RefreshTokenBlacklist;
import com.shared.dao.AbstractDao;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@Transactional(readOnly = true)
public class TokenBlacklistDao extends AbstractDao<RefreshTokenBlacklist, UUID> {

    public TokenBlacklistDao() {
        super(RefreshTokenBlacklist.class);
    }

    public Optional<RefreshTokenBlacklist> findByToken(String token) {
        String jpql = "SELECT t FROM RefreshTokenBlacklist t WHERE t.token = :token";
        return entityManager.createQuery(jpql, RefreshTokenBlacklist.class)
                .setParameter("token", token)
                .getResultStream()
                .findFirst();
    }

    public boolean isBlacklisted(String token) {
        String jpql = "SELECT COUNT(t) FROM RefreshTokenBlacklist t WHERE t.token = :token";
        Long count = entityManager.createQuery(jpql, Long.class)
                .setParameter("token", token)
                .getSingleResult();
        return count > 0;
    }

    public List<RefreshTokenBlacklist> findByUserId(UUID userId) {
        String jpql = "SELECT t FROM RefreshTokenBlacklist t WHERE t.userId = :userId";
        return entityManager.createQuery(jpql, RefreshTokenBlacklist.class)
                .setParameter("userId", userId)
                .getResultList();
    }

    @Transactional
    public int deleteExpiredTokens(LocalDateTime now) {
        String jpql = "DELETE FROM RefreshTokenBlacklist t WHERE t.expiresAt <= :now";
        return entityManager.createQuery(jpql)
                .setParameter("now", now)
                .executeUpdate();
    }
}