package auth.service;

import auth.dao.IRolesDao;
import auth.dao.IUserDao;
import auth.dao.IUserRoleDao;
import auth.entity.RoleNames;
import auth.entity.Roles;
import auth.entity.User;
import com.shared.dto.auth.CreateRoleRequest;
import com.shared.dto.auth.RoleDto;
import com.shared.dto.auth.UpdateRoleRequest;
import com.shared.exception.EntityNotFoundException;
import com.shared.service.IRolesService;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

@Component("rolesService")
public class RolesService implements IRolesService {

    private final IRolesDao rolesDao;
    private final IUserDao userDao;

    public RolesService(IRolesDao rolesDao, IUserDao userDao) {
        this.rolesDao = rolesDao;
        this.userDao = userDao;
    }

    @Override
    public RoleDto createRole(CreateRoleRequest request) {
        Roles entity = Roles.builder()
                .name(RoleNames.valueOf(request.name()))
                .description(request.description())
                .build();
        return mapToDto(rolesDao.save(entity));
    }

    @Override
    public RoleDto getRoleById(UUID id) {
        Roles role = rolesDao.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Роль с ID " + id + " не найдена"));
        return mapToDto(role);
    }

    @Override
    public List<RoleDto> getAllRoles() {
        List<RoleDto> result = new ArrayList<>();
        for (Roles role : rolesDao.findAll()) {
            result.add(mapToDto(role));
        }
        return result;
    }

    @Override
    public RoleDto updateRole(UUID id, UpdateRoleRequest request) {
        Roles existing = rolesDao.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Роль с ID " + id + " не найдена"));

        if (request.description() != null) {
            existing.setDescription(request.description());
        }

        rolesDao.update(existing);
        return mapToDto(existing);
    }

    @Override
    public boolean deleteRole(UUID id) {
        Roles role = rolesDao.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Роль с ID " + id + " не найдена"));

        // Каскадно отзываем роль у всех связанных пользователей
        for (User user : new HashSet<>(role.getUsers())) {
            user.revokeRole(role);
            userDao.update(user);
        }

        return rolesDao.deleteById(id);
    }

    private RoleDto mapToDto(Roles role) {
        return new RoleDto(role.getId(), role.getName().getValue(), role.getDescription());
        //время создание на время или насовсем убрано
    }
}
