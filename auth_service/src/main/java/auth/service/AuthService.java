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
import com.shared.dto.auth.LoginRequest;
import com.shared.dto.auth.RegisterRequest;
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
    public AuthResponse register(RegisterRequest request) {
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

        String defaultRoleName = RoleNames.ROLE_MANAGER.getValue();

        Roles role = rolesDao.findByName(defaultRoleName).orElseGet(() ->
                rolesDao.save(Roles.builder()
                        .name(defaultRoleName)
                        .description("Автоматически создана при регистрации")
                        .build())
        );

        savedUser.addRole(role, savedUser.getId());

        log.info("Успешно зарегистрирован новый пользователь: {}", savedUser.getUsername());

        // Маппим сохраненного пользователя со всеми проинициализированными ролями в AuthResponse
        return userMapper.toAuthResponse(savedUser);
    }

    @Override
    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        // Ищем пользователя по username или считаем, что в поле username мог быть передан email
        User user = userDao.findByUsername(request.username())
                .or(() -> userDao.findByEmail(request.username()))
                .orElseThrow(() -> new BadCredentialsException("Неверное имя пользователя или пароль"));

        // Проверяем соответствие сырого пароля хэшу из базы данных
        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new BadCredentialsException("Неверное имя пользователя или пароль");
        }

        // Подгружаем роли через JOIN FETCH, чтобы избежать LazyInitializationException вне транзакции
        User userWithRoles = userDao.findByIdWithRolesJoinFetch(user.getId())
                .orElse(user);

        log.debug("Пользователь {} успешно прошел проверку пароля", user.getUsername());
        return userMapper.toAuthResponse(userWithRoles);
    }
}