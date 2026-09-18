package auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shared.CrudController;
import com.shared.dto.auth.UpdateUserRequest;
import com.shared.dto.auth.UserDto;
import com.shared.dto.auth.CreateUserRequest;
import com.shared.dto.auth.AssignRoleRequest;
import com.shared.dto.auth.RevokeRoleRequest;
import com.shared.service.IUserService;
import com.shared.dto.DeleteStatus;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component("userController")
public class UserController extends AbstractAuthController
        implements CrudController<CreateUserRequest, UserDto, UpdateUserRequest, UUID> {

    private final IUserService userService;

    public UserController(IUserService userService, ObjectMapper objectMapper) {
        super(objectMapper, "userController");
        this.userService = userService;
    }

    @Override
    public String processCreate(CreateUserRequest request) {
        return toJson(userService.createUser(request));
    }

    @Override
    public String processGetById(UUID id) {
        return toJson(userService.getUserById(id));
    }

    @Override
    public String processGetAll() {
        return toJson(userService.getAllUsers());
    }

    @Override
    public String processUpdate(UUID id, UpdateUserRequest request) {
        return toJson(userService.updateUser(id, request));
    }

    @Override
    public String processDelete(UUID id) {
        boolean deleted = userService.deleteUser(id);
        return toJson(new DeleteStatus(deleted, "Пользователь c ID " + id + " обработан"));
    }

    /** Доп. вариант использования сервиса — назначение роли (many-to-many). */
    public String processAssignRole(AssignRoleRequest request) {
        return toJson(userService.assignRole(request));
    }

    /** Доп. вариант использования сервиса — отзыв роли (many-to-many). */
    public String processRevokeRole(RevokeRoleRequest request) {
        return toJson(userService.revokeRole(request));
    }

    @Override
    public String execute() { return "userController активен"; }

}