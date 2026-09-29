package auth.service;

import auth.dao.RolesDao;
import auth.dao.UserDao;
import auth.entity.RoleNames;
import auth.entity.Roles;
import auth.entity.User;
import com.shared.dto.auth.*;
import com.shared.exception.EntityNotFoundException;
import com.shared.service.IUserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

import java.util.stream.Collectors;

@Service("userService")
public class UserService implements IUserService {

    private final UserDao userDao;
    private final UserRoleService userRoleService;

    public UserService(UserDao userDao, UserRoleService userRoleService) {
        this.userDao = userDao;
        this.userRoleService = userRoleService;
    }

    @Override
    @Transactional
    public UserDto createUser(CreateUserRequest request) {
        // 1. Создаем пользователя
        User user = User.builder()
                .username(request.username())
                .passwordHash(request.passwordHash())
                .email(request.email())
                .registeredAt(LocalDateTime.now())
                .build();

        User savedUser = userDao.save(user);

        // 2. Привязываем роли через агрегат User (addRole), без ролиDao
        if (request.roleNames() != null && !request.roleNames().isEmpty()) {
            for (String roleNameStr : request.roleNames()) {
                RoleNames roleName = RoleNames.fromValue(roleNameStr);

                Roles role = userDao.getEntityManager()
                        .createQuery("SELECT r FROM Roles r WHERE r.name = :name", Roles.class)
                        .setParameter("name", roleName.getValue())
                        .getResultStream()
                        .findFirst()
                        .orElseThrow(() -> new EntityNotFoundException("Роль " + roleNameStr + " не найдена"));

                savedUser.addRole(role, savedUser.getId());
            }
        }

        return toDto(savedUser);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDto getUserById(UUID id) {
        User user = userDao.findByIdWithRolesJoinFetch(id)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь с ID " + id + " не найден"));
        return toDto(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> getAllUsers() {
        // Используем EntityGraph для подгрузки ролей за 1 SQL-запрос
        return userDao.findAllWithRoles().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public UserDto updateUser(UUID id, UpdateUserRequest request) {
        User user = userDao.findByIdWithRolesJoinFetch(id)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь с ID " + id + " не найден"));

        if (request.username() != null) {
            user.setUsername(request.username());
        }
        if (request.email() != null) {
            user.setEmail(request.email());
        }

        return toDto(user);
    }

    @Override
    @Transactional
    public boolean deleteUser(UUID id) {
        return userDao.deleteById(id);
    }

    @Override
    @Transactional
    public UserDto assignRole(AssignRoleRequest request) {
        userRoleService.link(request.userId(), request.roleId(), request.assignedBy());

        User user = userDao.findByIdWithRolesJoinFetch(request.userId())
                .orElseThrow(() -> new EntityNotFoundException("Пользователь с ID " + request.userId() + " не найден"));

        return toDto(user);
    }

    @Override
    @Transactional
    public UserDto revokeRole(RevokeRoleRequest request) {
        userRoleService.unlink(request.userId(), request.roleId());

        User user = userDao.findByIdWithRolesJoinFetch(request.userId())
                .orElseThrow(() -> new EntityNotFoundException("Пользователь с ID " + request.userId() + " не найден"));

        return toDto(user);
    }

    /**
     * Преобразование сущности User в DTO
     */
    private UserDto toDto(User user) {
        List<UserRoleInfoDto> roleDtos = user.getUserRoles() == null ? List.of() :
                user.getUserRoles().stream()
                        .map(ur -> new UserRoleInfoDto(
                                ur.getRole().getId(),
                                ur.getRole().getName(), // или ur.getRole().getName().getValue(), если наименование хранится в enum
                                ur.getAssignedAt(),
                                ur.getAssignedBy()
                        ))
                        .collect(Collectors.toList());


        return new UserDto(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getPasswordHash(),
                user.getRegisteredAt(),
                roleDtos
        );
    }
}