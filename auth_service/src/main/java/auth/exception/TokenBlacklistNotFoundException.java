package auth.exception;

import com.shared.exception.EntityNotFoundException;

import java.util.UUID;

public class TokenBlacklistNotFoundException extends EntityNotFoundException {
    public TokenBlacklistNotFoundException(String message) {
        super(message);
    }

    public TokenBlacklistNotFoundException(UUID id) {
        super("табица не найдена с Id" + id);
    }
}
