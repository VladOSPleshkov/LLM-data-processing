package auth.dao;

import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository("authDao")
public class InMemoryAuthDao implements AuthDao{

    private final Map<String, String> userCredentialsTable = new ConcurrentHashMap<>();
    private final Map<String, Long> userIdTable = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);
    @Override
    public Long saveUser(String username, String passwordHash) {
        if (userCredentialsTable.containsKey(username)) {
            throw new RuntimeException("Пользователь с таким именем уже существует");
        }
        Long newId = idGenerator.getAndIncrement();
        userCredentialsTable.put(username, passwordHash);
        userIdTable.put(username, newId);
        return newId;
    }
    @Override
    public Optional<String> findPasswordHashByUsername(String username) {
        return Optional.ofNullable(userCredentialsTable.get(username));
    }
    @Override
    public Optional<Long> findIdByUsername(String username) {
        return Optional.ofNullable(userIdTable.get(username));
    }
}
