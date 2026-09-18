package auth.dao;

import com.shared.dao.AbstractJdbcDao;
import com.shared.tx.ConnectionHolder;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository("authDao")
public class JdbcAuthDao extends AbstractJdbcDao implements AuthDao {

    private final Map<String, String> userCredentialsTable = new ConcurrentHashMap<>();
    private final Map<String, UUID> userIdTable = new ConcurrentHashMap<>();

    protected JdbcAuthDao(ConnectionHolder connectionHolder, String dbKey) {
        super(connectionHolder, dbKey);
    }

    @Override
    public UUID saveUser(String username, String passwordHash) {
        if (userCredentialsTable.containsKey(username)) {
            throw new RuntimeException("Пользователь с таким именем уже существует");
        }
        UUID newId =  UUID.randomUUID();
        userCredentialsTable.put(username, passwordHash);
        userIdTable.put(username, newId);
        return newId;
    }
    @Override
    public Optional<String> findPasswordHashByUsername(String username) {
        return Optional.ofNullable(userCredentialsTable.get(username));
    }
    @Override
    public Optional<UUID> findIdByUsername(String username) {
        return Optional.ofNullable(userIdTable.get(username));
    }
}
