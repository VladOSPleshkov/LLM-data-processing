package auth.controller;

import com.shared.dto.auth.CreateRoleRequest;
import com.shared.dto.auth.RoleDto;
import com.shared.dto.auth.UpdateRoleRequest;
import auth.service.IRolesService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/roles")
public class RolesController {

    private final IRolesService rolesService;

    public RolesController(IRolesService rolesService) {
        this.rolesService = rolesService;
    }

    @GetMapping
    public ResponseEntity<List<RoleDto>> getAllRoles() {
        return ResponseEntity.ok(rolesService.getAllRoles());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RoleDto> getRoleById(@PathVariable UUID id) {
        return ResponseEntity.ok(rolesService.getRoleById(id));
    }

    @PostMapping
    public ResponseEntity<RoleDto> createRole(@RequestBody CreateRoleRequest request) {
        RoleDto created = rolesService.createRole(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RoleDto> updateRole(@PathVariable UUID id, @RequestBody UpdateRoleRequest request) {
        return ResponseEntity.ok(rolesService.updateRole(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRole(@PathVariable UUID id) {
        rolesService.deleteRole(id);
        return ResponseEntity.noContent().build();
    }
}