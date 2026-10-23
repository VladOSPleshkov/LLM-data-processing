package auth.service;

import auth.dao.RolesDao;
import auth.dao.UserDao;
import auth.entity.RoleNames;
import auth.entity.Roles;
import auth.entity.User;
import auth.exception.UserAlreadyExistsException;
import auth.exception.UserNotFoundException;
import auth.mapper.UserMapper;
import com.shared.dto.auth.AuthResponse;
import com.shared.dto.auth.LoginRequest;
import com.shared.dto.auth.RegisterRequest;
import auth.service.IAuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

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
    public AuthResponse register(RegisterRequest request) {
        if (userDao.findByUsername(request.username()).isPresent()) {
            throw new UserAlreadyExistsException(request.username());
        }

        User newUser = User.builder()
                .username(request.username())
                .passwordHash(passwordEncoder.encode(request.password()))
                .email(request.email())
                .registeredAt(LocalDateTime.now())
                .build();

        User savedUser = userDao.save(newUser);

        String defaultRoleName = RoleNames.ROLE_MANAGER.getValue();

        Roles role = rolesDao.findByName(defaultRoleName).orElseGet(() ->
                rolesDao.save(Roles.builder()
                        .name(defaultRoleName)
                        .description("Автоматически создана при регистрации")
                        .build())
        );

        savedUser.addRole(role, savedUser.getId());

        log.info("Успешно зарегистрирован новый пользователь: {}", savedUser.getUsername());
        return userMapper.toAuthResponse(savedUser);
    }

    @Override
    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        User user = userDao.findByUsername(request.username())
                .or(() -> userDao.findByEmail(request.username()))
                .orElseThrow(() -> new BadCredentialsException("Неверное имя пользователя или пароль"));

        // Проверяем соответствие сырого пароля хэшу из базы данных
        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new BadCredentialsException("Неверное имя пользователя или пароль");
        }
        User userWithRoles = userDao.findByIdWithRolesJoinFetch(user.getId())
                .orElse(user);

        log.debug("Пользователь {} успешно прошел проверку пароля", user.getUsername());
        return userMapper.toAuthResponse(userWithRoles);
    }
    @Override
    @Transactional(readOnly = true)
    public AuthResponse getUserForRefresh(UUID userId) {
        User user = userDao.findByIdWithRolesJoinFetch(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
        return userMapper.toAuthResponse(user);
    }
}