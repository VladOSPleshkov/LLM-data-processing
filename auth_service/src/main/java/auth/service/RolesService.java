package auth.service;

import auth.dao.RolesDao;
import auth.entity.RoleNames;
import auth.entity.Roles;
import com.shared.dto.auth.CreateRoleRequest;
import com.shared.dto.auth.RoleDto;
import com.shared.dto.auth.UpdateRoleRequest;
import com.shared.exception.EntityNotFoundException;
import com.shared.service.IRolesService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service("rolesService")
public class RolesService implements IRolesService {

    private final RolesDao rolesDao;

    public RolesService(RolesDao rolesDao) {
        this.rolesDao = rolesDao;
    }

    @Override
    @Transactional
    public RoleDto createRole(CreateRoleRequest request) {
        RoleNames roleName = RoleNames.fromValue(request.name());

        Roles role = rolesDao.findByName(String.valueOf(roleName))
                .orElseGet(() -> rolesDao.save(Roles.builder()
                        .name(String.valueOf(roleName))
                        .description(request.description())
                        .build()));

        return toDto(role);
    }

    @Override
    @Transactional(readOnly = true)
    public RoleDto getRoleById(UUID id) {
        Roles role = rolesDao.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Роль с ID " + id + " не найдена"));
        return toDto(role);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RoleDto> getAllRoles() {
        return rolesDao.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public RoleDto updateRole(UUID id, UpdateRoleRequest request) {
        Roles role = rolesDao.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Роль с ID " + id + " не найдена"));

        // Изменяем описание — Dirty Checking автоматически сгенерирует UPDATE
        role.setDescription(request.description());

        return toDto(role);
    }

    @Override
    @Transactional
    public boolean deleteRole(UUID id) {
        return rolesDao.deleteById(id);
    }

    private RoleDto toDto(Roles role) {
        return new RoleDto(role.getId(), role.getName(), role.getDescription());
    }
}