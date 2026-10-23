package auth.service;

import auth.dao.RolesDao;
import auth.dao.UserDao;
import auth.entity.RoleNames;
import auth.entity.Roles;
import auth.entity.User;
import auth.exception.RoleNotFoundException;
import auth.exception.UserNotFoundException;
import auth.mapper.UserMapper;
import com.shared.dto.auth.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

import java.util.stream.Collectors;

@Service("userService")
@RequiredArgsConstructor
public class UserService implements IUserService {

    private final UserDao userDao;
    private final RolesDao rolesDao;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public UserDto createUser(CreateUserRequest request) {
        User user = User.builder()
                .username(request.username())
                .passwordHash(request.passwordHash())
                .email(request.email())
                .registeredAt(LocalDateTime.now())
                .build();

        User savedUser = userDao.save(user);

        if (request.roleNames() != null) {
            for (String roleNameStr : request.roleNames()) {
                RoleNames roleName = RoleNames.fromValue(roleNameStr);
                Roles role = rolesDao.findByName(roleName.getValue())
                        .orElseThrow(() -> RoleNotFoundException.byName(roleNameStr));
                savedUser.addRole(role, savedUser.getId());
            }
        }

        return userMapper.toDto(savedUser);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDto getUserById(UUID id) {
        return userMapper.toDto(userDao.findByIdWithRolesJoinFetch(id)
                .orElseThrow(() -> new UserNotFoundException(id)));
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> getAllUsers() {
        return userDao.findAllWithRoles().stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public UserDto updateUser(UUID id, UpdateUserRequest request) {
        User user = userDao.findByIdWithRolesJoinFetch(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        if (request.username() != null) {
            user.setUsername(request.username());
        }
        if (request.email() != null) {
            user.setEmail(request.email());
        }
        return userMapper.toDto(user);
    }

    @Override
    @Transactional
    public boolean deleteUser(UUID id) {
        return userDao.deleteById(id);
    }

    @Override
    @Transactional
    public UserDto assignRole(AssignRoleRequest request) {
        User user = userDao.findByIdWithRolesJoinFetch(request.userId())
                .orElseThrow(() -> new UserNotFoundException(request.userId()));
        Roles role = rolesDao.findById(request.roleId())
                .orElseThrow(() -> new RoleNotFoundException(request.roleId()));

        user.addRole(role, request.assignedBy());
        return userMapper.toDto(user);
    }

    @Override
    @Transactional
    public UserDto revokeRole(RevokeRoleRequest request) {
        User user = userDao.findByIdWithRolesJoinFetch(request.userId())
                .orElseThrow(() -> new UserNotFoundException(request.userId()));
        Roles role = rolesDao.findById(request.roleId())
                .orElseThrow(() -> new RoleNotFoundException(request.roleId()));

        user.removeRole(role);
        return userMapper.toDto(user);
    }
}