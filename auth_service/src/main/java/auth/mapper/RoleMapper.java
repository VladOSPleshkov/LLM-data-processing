package auth.mapper;

import auth.entity.Roles;
import com.shared.dto.auth.RoleDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RoleMapper {
    RoleDto toDto(Roles role);
}
