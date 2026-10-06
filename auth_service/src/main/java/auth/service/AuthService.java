package auth.service;

import auth.dao.RolesDao;
import auth.dao.UserDao;
import auth.entity.RoleNames;
import auth.entity.Roles;
import auth.entity.User;
import auth.exception.UserNotFoundException;
import auth.mapper.UserMapper;
import com.shared.dto.auth.AuthRequest;
import com.shared.dto.auth.AuthResponse;
import com.shared.exception.EntityNotFoundException;
import com.shared.service.IAuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service("authService")
@RequiredArgsConstructor
public class AuthService implements IAuthService {

    private final UserDao userDao;
    private final RolesDao rolesDao;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public AuthResponse register(AuthRequest request) {
        if (userDao.findByUsername(request.username()).isPresent()) {
            throw new UserNotFoundException(request.username());
        }

        User newUser = User.builder()
                .username(request.username())
                .passwordHash(passwordEncoder.encode(request.password()))
                .email(request.email())
                .registeredAt(LocalDateTime.now())
                .build();

        // Persist выполняется ВНУТРИ транзакции, открытой TransactionInterceptor'ом
        // для этого метода (транзакция привязана к authTransactionManager, т.к. он @Primary).
        User savedUser = userDao.save(newUser);

        List<String> requestedRoles = (request.roles() == null || request.roles().isEmpty())
                ? List.of(RoleNames.ROLE_MANAGER.getValue())
                : request.roles();

        for (String roleNameStr : requestedRoles) {
            RoleNames roleName = RoleNames.fromValue(roleNameStr);
            Roles role = rolesDao.findByName(roleName.getValue())
                    .orElseGet(() -> rolesDao.save(Roles.builder()
                            .name(roleName.getValue())
                            .description("Автоматически создана при регистрации")
                            .build()));
            savedUser.addRole(role, savedUser.getId());
        }
        // в текущем persistence context, а CascadeType.ALL на User.userRoles
        // гарантирует persist новых UserRoles при флаше/коммите транзакции.

        log.info("Зарегистрирован новый пользователь: {}", savedUser.getUsername());
        return userMapper.toAuthResponse(savedUser);
    }

    @Override
    @Transactional(readOnly = true)
    public AuthResponse login(AuthRequest request) {
        User user = userDao.findByUsername(request.username())
                .or(() -> request.email() != null ? userDao.findByEmail(request.email()) : Optional.empty())
                .orElseThrow(() -> new BadCredentialsException("Неверное имя пользователя или пароль"));

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new BadCredentialsException("Неверное имя пользователя или пароль");
        }

        User userWithRoles = userDao.findByIdWithRolesJoinFetch(user.getId()).orElse(user);
        return userMapper.toAuthResponse(userWithRoles);
    }
}