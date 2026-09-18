package auth.dao;

import auth.entity.Roles;
import com.shared.exception.EntityNotFoundException;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository("rolesDao")
public class InMemoryRolesDao implements IRolesDao {

    private final Map<Long, Roles> storage = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    @Override
    public Roles save(Roles role) {
        Long newId = idGenerator.getAndIncrement();
        Roles saved = Roles.builder()
                .id(newId)
                .name(role.getName())
                .description(role.getDescription())
                .users(role.getUsers())
                .build();
        storage.put(newId, saved);
        return saved;
    }

    @Override
    public Optional<Roles> findById(Long id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public Optional<Roles> findByName(String name) {
        return storage.values().stream()
                .filter(r -> r.getName().equalsIgnoreCase(name)).findFirst();
    }

    @Override
    public List<Roles> findAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public Roles update(Roles role) {
        if (!storage.containsKey(role.getId())) {
            throw new EntityNotFoundException("Роль с ID " + role.getId() + " не найдена для обновления");
        }
        storage.put(role.getId(), role);
        return role;
    }

    @Override
    public boolean deleteById(Long id) {
        return storage.remove(id) != null;
    }

    @Override
    public Long findOrCreateRole(String name) {
        return findByName(name)
                .map(Roles::getId)
                .orElseGet(() -> save(Roles.builder().name(name).description("Автоматически создана")
                        .build()).getId());
    }

    @Override
    public String findNameById(Long id) {
        return findById(id)
                .map(Roles::getName)
                .orElseThrow(() -> new EntityNotFoundException("Роль с ID " + id + " не найдена"));
    }
}
