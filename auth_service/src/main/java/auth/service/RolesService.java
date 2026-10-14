package auth.service;

import auth.dao.RolesDao;
import auth.entity.RoleNames;
import auth.entity.Roles;
import auth.exception.RoleAlreadyExistException;
import auth.exception.RoleNotFoundException;
import auth.mapper.RoleMapper;
import com.shared.dto.auth.CreateRoleRequest;
import com.shared.dto.auth.RoleDto;
import com.shared.dto.auth.UpdateRoleRequest;
import com.shared.exception.BadRequestException;
import com.shared.exception.EntityNotFoundException;
import com.shared.service.IRolesService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;


@Service("rolesService")
@RequiredArgsConstructor
public class RolesService implements IRolesService {

    private final RolesDao rolesDao;
    private final RoleMapper roleMapper;

    @Override
    @Transactional
    public RoleDto createRole(CreateRoleRequest request) {
        RoleNames roleName = RoleNames.fromValue(request.name());
        Roles role = rolesDao.findByName(roleName.getValue())
                .orElseGet(() -> rolesDao.save(Roles.builder()
                        .name(roleName.getValue())
                        .description(request.description())
                        .build()));
        return roleMapper.toDto(role);
    }

    @Override
    @Transactional(readOnly = true)
    public RoleDto getRoleById(UUID id) {
        return roleMapper.toDto(rolesDao.findById(id)
                .orElseThrow(() -> new RoleNotFoundException(id)));
    }

    @Override
    @Transactional(readOnly = true)
    public List<RoleDto> getAllRoles() {
        return rolesDao.findAll().stream()
                .map(roleMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public RoleDto updateRole(UUID id, UpdateRoleRequest request) {
        Roles role = rolesDao.findById(id)
                .orElseThrow(() -> new RoleNotFoundException(id));
        role.setDescription(request.description());
        return roleMapper.toDto(role);
    }

    @Override
    @Transactional
    public boolean deleteRole(UUID id) {
        return rolesDao.deleteById(id);
    }
}