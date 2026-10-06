
import auth.dao.UserDao;
import auth.entity.User;
import auth.service.UserService;
import com.shared.dto.auth.CreateUserRequest;
import com.shared.dto.auth.UserDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserDao userDao;

    @InjectMocks
    private UserService userService;

    @Test
    @DisplayName("Создание пользователя с проверкой сохранения в репозитории")
    void createUser_Success() {
        // Arrange
        CreateUserRequest request = new CreateUserRequest("john_doe", "password123", "john@example.com", Collections.singletonList("ROLE_MANAGER"));
        UUID userId = UUID.randomUUID();

        User savedUser = User.builder()
                .id(userId)
                .username("john_doe")
                .email("john@example.com")
                .registeredAt(LocalDateTime.now())
                .build();

        when(userDao.save(any(User.class))).thenReturn(savedUser);
        UserDto result = userService.createUser(request);

        assertNotNull(result);
        assertEquals(userId, result.id());
        assertEquals("john_doe", result.username());

        verify(userDao, times(1)).save(any(User.class));
    }
}
