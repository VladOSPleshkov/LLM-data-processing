package auth.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.shared.CrudController;
import com.shared.dto.auth.CreateRoleRequest;
import com.shared.dto.auth.RoleDto;
import com.shared.dto.auth.UpdateRoleRequest;
import com.shared.service.IRolesService;
import org.springframework.stereotype.Component;
import com.shared.dto.DeleteStatus;

import java.util.UUID;

@Component("rolesController")
public class RolesController extends AbstractAuthController
        implements CrudController<CreateRoleRequest, RoleDto, UpdateRoleRequest, UUID> {

    private final IRolesService rolesService;
    public RolesController(IRolesService rolesService, ObjectMapper objectMapper) {
        super(objectMapper, "rolesController");
        this.rolesService = rolesService;}

    @Override
    public String processCreate(CreateRoleRequest request) { return toJson(rolesService.createRole(request)); }

    @Override
    public String processGetById(UUID id) { return toJson(rolesService.getRoleById(id)); }

    @Override
    public String processGetAll() { return toJson(rolesService.getAllRoles()); }

    @Override
    public String processUpdate(UUID id, UpdateRoleRequest request) { return toJson(rolesService.updateRole(id, request)); }

    @Override
    public String processDelete(UUID id) {
        boolean deleted = rolesService.deleteRole(id);
        return toJson(new DeleteStatus(deleted, "Роль с ID " + id + " обработана"));
    }

    @Override
    public String execute() { return "rolesController активен"; }

}