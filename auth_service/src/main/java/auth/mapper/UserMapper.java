package auth.mapper;


import auth.entity.User;
import auth.entity.UserRoles;
import com.shared.dto.auth.AuthResponse;
import com.shared.dto.auth.UserDto;
import com.shared.dto.auth.UserRoleInfoDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Вызывается ИСКЛЮЧИТЕЛЬНО внутри @Transactional-методов сервиса,
 * пока EntityManager/persistence context ещё открыт — иначе обращение
 * к ленивой ассоциации UserRoles.role внутри toRoleInfoDto()
 * выбросит LazyInitializationException.
 */
@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "roles", source = "userRoles")
    UserDto toDto(User user);

    @Mapping(target = "roleId", source = "role.id")
    @Mapping(target = "roleName", source = "role.name")
    UserRoleInfoDto toRoleInfoDto(UserRoles userRoles);

    default AuthResponse toAuthResponse(User user) {
        List<String> roleNames = user.getUserRoles() == null
                ? List.of()
                : user.getUserRoles().stream()
                .map(ur -> ur.getRole().getName())
                .collect(Collectors.toList());
        return new AuthResponse(user.getId(), user.getUsername(), roleNames);
    }
}
