package auth.service;

import auth.dao.RolesDao;
import auth.dao.UserDao;
import auth.entity.RoleNames;
import auth.entity.Roles;
import auth.entity.User;
import com.shared.dto.auth.AuthRequest;
import com.shared.dto.auth.AuthResponse;
import com.shared.exception.EntityNotFoundException;
import com.shared.service.IAuthService;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

//@Component("authService")
@Service("authService")
public class AuthService implements IAuthService {

    private final UserDao userDao;
    private final RolesDao rolesDao;

    public AuthService(UserDao userDao, RolesDao rolesDao) {
        this.userDao = userDao;
        this.rolesDao = rolesDao;
    }

    @Override
    @Transactional
    public AuthResponse register(AuthRequest request) {
        // 1. Создаем сущность нового пользователя
        User newUser = User.builder()
                .username(request.username())
                .passwordHash(request.passwordHash())
                .email(request.email())
                .registeredAt(LocalDateTime.now())
                .build();

        // 2. Сохраняем пользователя (переходит в состояние Managed)
        User savedUser = userDao.save(newUser);

        // 3. Назначаем запрошенные роли
        List<String> requestedRoles = request.roles() == null ? List.of() : request.roles();
        for (String roleNameStr : requestedRoles) {
            RoleNames roleName = RoleNames.fromValue(roleNameStr);

            Roles role = rolesDao.findByName(String.valueOf(roleName))
                    .orElseGet(() -> rolesDao.save(Roles.builder()
                            .name(String.valueOf(roleName))
                            .description("Автоматически создана при регистрации")
                            .build()));

            // Добавляем связь через метод сущности — каскад сохрани запись в user_roles
            savedUser.addRole(role, savedUser.getId());
        }

        // userDao.update() НЕ ТРЕБУЕТСЯ — Hibernate применит Dirty Checking при завершении транзакции
        return new AuthResponse(
                savedUser.getId(),
                savedUser.getUsername(),
                requestedRoles
        );
    }

    @Override
    @Transactional(readOnly = true)
    public AuthResponse login(AuthRequest request) {
        // Ищем пользователя по логину или email
        User user = userDao.findByUsername(request.username())
                .or(() -> userDao.findByEmail(request.email()))
                .orElseThrow(() -> new EntityNotFoundException("Пользователь не найден"));

        // Подгружаем пользователя с его ролями за 1 SQL-запрос через JOIN FETCH / EntityGraph
        User userWithRoles = userDao.findByIdWithRolesJoinFetch(user.getId())
                .orElse(user);

        List<String> roleNames = userWithRoles.getUserRoles().stream()
                .map(ur -> ur.getRole().getName())
                .collect(Collectors.toList());

        return new AuthResponse(
                userWithRoles.getId(),
                userWithRoles.getUsername(),
                roleNames
        );
    }
}