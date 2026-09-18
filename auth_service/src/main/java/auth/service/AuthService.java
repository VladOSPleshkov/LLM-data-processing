package auth.service;

import auth.dao.IRolesDao;
import auth.dao.IUserDao;
import auth.entity.Roles;
import auth.entity.User;
import com.shared.dto.auth.AuthRequest;
import com.shared.dto.auth.AuthResponse;
import com.shared.exception.EntityNotFoundException;
import com.shared.service.IAuthService;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component("authService")
public class AuthService implements IAuthService {

    private final IUserDao userDao;
    private final IRolesDao rolesDao;

    public AuthService(IUserDao userDao, IRolesDao rolesDao) {
        this.userDao = userDao;
        this.rolesDao = rolesDao;
    }

    @Override
    public AuthResponse register(AuthRequest request) {
        User newUser = User.builder()
                .username(request.username())
                .passwordHash(request.passwordHash())
                .email(request.email())
                .registeredAt(LocalDateTime.now())
                .build();

        User savedUser = userDao.save(newUser);

        List<String> requestedRoles = request.roles() == null ? List.of() : request.roles();
        for (String roleName : requestedRoles) {
            UUID roleId = rolesDao.findOrCreateRole(roleName);
            Roles role = rolesDao.findById(roleId).orElseThrow();
            savedUser.assignRole(role, savedUser.getId()); // самоназначение при регистрации
            role.addUser(savedUser);
            rolesDao.update(role);
        }
        userDao.update(savedUser);

        return new AuthResponse(
                savedUser.getId(),
                savedUser.getUsername(),
                requestedRoles
        );
    }

    @Override
    public AuthResponse login(AuthRequest request) {
        User user = userDao.findByPasswordHash(request.passwordHash())
                .or(() -> userDao.findByUsername(request.username()))
                .orElseThrow(() -> new EntityNotFoundException("Пользователь не найден"));

        List<String> roleNames = user.getUserRoles().stream()
                .map(ur -> ur.getRole().getValue())
                .collect(Collectors.toList());

        return new AuthResponse(
                user.getId(),
                user.getUsername(),
                roleNames
        );
    }
}