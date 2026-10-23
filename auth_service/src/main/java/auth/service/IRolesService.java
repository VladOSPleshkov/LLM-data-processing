package auth.service;

import com.shared.dto.auth.CreateRoleRequest;
import com.shared.dto.auth.UpdateRoleRequest;
import com.shared.dto.auth.RoleDto;

import java.util.List;
import java.util.UUID;

public interface IRolesService {
    RoleDto createRole(CreateRoleRequest request);
    RoleDto getRoleById(UUID id);
    List<RoleDto> getAllRoles();
    RoleDto updateRole(UUID id, UpdateRoleRequest request);
    boolean deleteRole(UUID id);
}
