package auth.service;

import auth.dao.RolesDao;
import auth.dao.UserDao;
import auth.dao.UserRolesDao;
import auth.entity.*;
import com.shared.exception.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service("userRoleService")
public class UserRoleService {

    private final UserRolesDao userRolesDao;
    private final UserDao userDao;
    private final RolesDao rolesDao;

    public UserRoleService(UserRolesDao userRolesDao, UserDao userDao, RolesDao rolesDao) {
        this.userRolesDao = userRolesDao;
        this.userDao = userDao;
        this.rolesDao = rolesDao;
    }

    @Transactional
    public void link(UUID userId, UUID roleId, UUID assignedBy) {
        UserRoleId compositeKey = new UserRoleId(userId, roleId);

        if (userRolesDao.findById(compositeKey).isPresent()) { return; }
        if (userDao.findById(userId).isEmpty()) {
            throw new EntityNotFoundException("Пользователь не найден: " + userId);
        }
        if (rolesDao.findById(roleId).isEmpty()) {
            throw new EntityNotFoundException("Роль не найдена: " + roleId);
        }

        User userRef = userRolesDao.getEntityManager().getReference(User.class, userId);
        Roles roleRef = userRolesDao.getEntityManager().getReference(Roles.class, roleId);

        UserRoles userRoles = UserRoles.builder()
                .id(compositeKey)
                .user(userRef)
                .role(roleRef)
                .assignedAt(LocalDateTime.now())
                .assignedBy(assignedBy)
                .build();

        userRolesDao.save(userRoles);
    }

    @Transactional
    public void unlink(UUID userId, UUID roleId) {
        UserRoleId compositeKey = new UserRoleId(userId, roleId);

        UserRoles userRole = userRolesDao.findById(compositeKey)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Связь пользователя " + userId + " и роли " + roleId + " не найдена"
                ));

        userRolesDao.delete(userRole);
    }

    @Transactional(readOnly = true)
    public List<UUID> findRoleIdsByUserId(UUID userId) {
        return userRolesDao.findRoleIdsByUserId(userId);
    }

    @Transactional
    public void deleteAllByUserId(UUID userId) {
        userRolesDao.deleteAllByUserId(userId);
    }

    @Transactional
    public void deleteAllByRoleId(UUID roleId) {
        userRolesDao.deleteAllByRoleId(roleId);
    }
}