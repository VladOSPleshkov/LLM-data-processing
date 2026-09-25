package auth.service;

import auth.dao.IRolesDao;
import auth.dao.IUserDao;
import auth.dao.IUserRoleDao;
import auth.entity.Roles;
import auth.entity.User;
import auth.entity.UserRole;
import com.shared.common.annotation.ManualTransactional;
import com.shared.dto.auth.AssignRoleRequest;
import com.shared.dto.auth.CreateUserRequest;
import com.shared.dto.auth.UpdateUserRequest;
import com.shared.dto.auth.UserDto;
import com.shared.dto.auth.UserRoleInfoDto;
import com.shared.dto.auth.RevokeRoleRequest;
import com.shared.exception.EntityNotFoundException;
import com.shared.service.IUserService;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.ArrayList;
import java.util.HashSet;

import java.util.stream.Collectors;

@Component("userService")
public class UserService implements IUserService {

    private static final String DB_KEY = "AUTH_DB";

    private final IUserDao userDao;
    private final IRolesDao rolesDao;
    private final IUserRoleDao userRoleDao;

    public UserService(IUserDao userDao, IRolesDao rolesDao, IUserRoleDao userRoleDao) {
        this.userDao = userDao;
        this.rolesDao = rolesDao;
        this.userRoleDao = userRoleDao;
    }

    @Override
    @ManualTransactional(dbKey = DB_KEY) // multi-step: user + N ролей в join-таблице
    public UserDto createUser(CreateUserRequest request) {
        User entity = User.builder()
                .username(request.username())
                .passwordHash(request.passwordHash())
                .email(request.email())
                .registeredAt(LocalDateTime.now())
                .build();
        User saved = userDao.save(entity);

        List<String> roleNames = request.roleNames() == null ? List.of() : request.roleNames();
        for (String roleName : roleNames) {
            UUID roleId = rolesDao.findOrCreateRole(roleName);
            userRoleDao.link(saved.getId(), roleId, saved.getId());
        }
        return mapToDto(saved);
    }

    @Override
    public UserDto getUserById(UUID id) {
        User user = userDao.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь с ID " + id + " не найден"));
        return mapToDto(user);
    }

    @Override
    public List<UserDto> getAllUsers() {
        return userDao.findAll().stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Override
    public UserDto updateUser(UUID id, UpdateUserRequest request) {
        User existing = userDao.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь с ID " + id + " не найден"));

        User updated = existing.toBuilder()
                .email(request.email() != null ? request.email() : existing.getEmail())
                .build();

        // Одна таблица, одно поле — формальная транзакция не нужна.
        User result = userDao.update(updated);
        if (!userDao.existsById(id)) {
            throw new IllegalStateException("Не удалось подтвердить обновление пользователя " + id);
        }

        if (request.roleNames() != null) {
            reassignRoles(id, request.roleNames());
        }
        return mapToDto(result);
    }

    @ManualTransactional(dbKey = DB_KEY) // удаление старых + вставка новых связей — атомарно
    protected void reassignRoles(UUID userId, List<String> roleNames) {
        userRoleDao.deleteAllByUserId(userId);
        for (String roleName : roleNames) {
            UUID roleId = rolesDao.findOrCreateRole(roleName);
            userRoleDao.link(userId, roleId, userId);
        }
    }

    @Override
    @ManualTransactional(dbKey = DB_KEY) // каскад: связи + сам пользователь
    public boolean deleteUser(UUID id) {
        userRoleDao.deleteAllByUserId(id);
        return userDao.deleteById(id);
    }

    @Override
    @ManualTransactional(dbKey = DB_KEY)
    public UserDto assignRole(AssignRoleRequest request) {
        User user = userDao.findById(request.userId())
                .orElseThrow(() -> new EntityNotFoundException("Пользователь с ID " + request.userId() + " не найден"));
        rolesDao.findById(request.roleId())
                .orElseThrow(() -> new EntityNotFoundException("Роль с ID " + request.roleId() + " не найдена"));

        userRoleDao.link(request.userId(), request.roleId(), request.assignedBy());
        return mapToDto(user);
    }

    @Override
    public UserDto revokeRole(RevokeRoleRequest request) {
        // Одна DELETE-операция над join-таблицей — транзакция избыточна.
        userRoleDao.unlink(request.userId(), request.roleId());
        User user = userDao.findById(request.userId())
                .orElseThrow(() -> new EntityNotFoundException("Пользователь не найден"));
        return mapToDto(user);
    }

    private UserDto mapToDto(User user) {
        List<UserRoleInfoDto> roles = userRoleDao.findRoleIdsByUserId(user.getId()).stream()
                .map(roleId -> rolesDao.findById(roleId)
                        .map(r -> new UserRoleInfoDto(r.getId(), r.getName().getValue(), LocalDateTime.now(), null))
                        .orElse(null))
                .filter(java.util.Objects::nonNull)
                .collect(Collectors.toList());

        return new UserDto(user.getId(), user.getUsername(), user.getEmail(),
                user.getPasswordHash(), user.getRegisteredAt(), roles);
    }
}