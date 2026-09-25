package auth.dao;

import auth.entity.RefreshTokenBlacklist;
import auth.entity.RoleNames;
import auth.entity.Roles;
import auth.entity.User;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

@Component
public class AuthDbMapper {

    public User userFromResultSet(ResultSet rs) throws SQLException {
        return User.builder()
                .id(rs.getObject("id", UUID.class))
                //id(UUID.fromString(rs.getString("id")))
                .passwordHash(rs.getString("password_hash"))
                .username(rs.getString("username"))
                .email(rs.getString("email"))
                .registeredAt(rs.getTimestamp("registered_at").toLocalDateTime())
                .build();
    }

    public Roles roleFromResultSet(ResultSet rs) throws SQLException {
        return Roles.builder()
                .id(rs.getObject("id", UUID.class))
                .name(RoleNames.fromValue(rs.getString("name")))
                .description(rs.getString("description"))
                .build();
    }

    public RefreshTokenBlacklist blacklistFromResultSet(ResultSet rs) throws SQLException {
        return RefreshTokenBlacklist.builder()
                .id(rs.getObject("id", UUID.class))
                .token(rs.getString("token"))
                .userId(UUID.fromString(rs.getString("user_id")))
                .blacklistedAt(rs.getTimestamp("blacklisted_at").toLocalDateTime())
                .expiresAt(rs.getTimestamp("expires_at").toLocalDateTime())
                .build();
    }
}