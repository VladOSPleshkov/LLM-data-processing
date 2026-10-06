package auth.controller;

import com.shared.dto.auth.UpdateUserRequest;
import com.shared.dto.auth.UserDto;
import com.shared.dto.auth.CreateUserRequest;
import com.shared.service.IUserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final IUserService userService;

    public UserController(IUserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<UserDto> processCreate(@RequestBody CreateUserRequest request) {
        UserDto created = userService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> processGetById(@PathVariable UUID id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @GetMapping
    public ResponseEntity<List<UserDto>> processGetAll() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDto> processUpdate(@PathVariable UUID id, @RequestBody UpdateUserRequest request) {
        return ResponseEntity.ok(userService.updateUser(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void>  processDelete(UUID id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

//    @PostMapping("/{id}/roles/{roleName}")
//    public ResponseEntity<UserDto> assignRole(@PathVariable UUID id, @PathVariable String roleName) {
//        return ResponseEntity.ok(userService.assignRoleToUser(id, roleName));
//    }
//
//    @DeleteMapping("/{id}/roles/{roleName}")
//    public ResponseEntity<UserDto> revokeRole(@PathVariable UUID id, @PathVariable String roleName) {
//        return ResponseEntity.ok(userService.revokeRoleFromUser(id, roleName));
//    }
}