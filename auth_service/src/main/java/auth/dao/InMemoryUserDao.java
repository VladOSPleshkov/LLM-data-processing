package auth.dao;

import auth.entity.User;
import com.shared.exception.DuplicateEntityException;
import com.shared.exception.EntityNotFoundException;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Repository("userDao")
public class InMemoryUserDao implements IUserDao {

    private final Map<UUID, User> storage = new ConcurrentHashMap<>();

    @Override
    public User save(User user) {
        boolean usernameTaken = storage.values().stream()
                .anyMatch(u -> u.getUsername().equalsIgnoreCase(user.getUsername()));
        if (usernameTaken) {
            throw new DuplicateEntityException("Пользователь с username '" + user.getUsername() + "' уже существует");
        }

        UUID newId = UUID.randomUUID();
        User saved = User.builder()
                .id(newId)
                .username(user.getUsername())
                .keycloakSub(user.getKeycloakSub())
                .email(user.getEmail())
                .registeredAt(user.getRegisteredAt())
                .userRoles(user.getUserRoles())
                .build();

        storage.put(newId, saved);
        return saved;
    }

    @Override
    public Optional<User> findById(UUID id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return storage.values().stream()
                .filter(u -> u.getUsername().equalsIgnoreCase(username))
                .findFirst();
    }

    @Override
    public Optional<User> findByKeycloakSub(String keycloakSub) {
        return storage.values().stream()
                .filter(u -> u.getKeycloakSub() != null && u.getKeycloakSub().equals(keycloakSub))
                .findFirst();
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public User update(User user) {
        if (!storage.containsKey(user.getId())) {
            throw new EntityNotFoundException("Пользователь с ID " + user.getId() + " не найден для обновления");
        }
        storage.put(user.getId(), user);
        return user;
    }

    @Override
    public boolean deleteById(UUID id) {
        return storage.remove(id) != null;
    }
}