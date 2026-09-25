package auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shared.CrudController;
import com.shared.dto.auth.CreateBlacklistTokenRequest;
import com.shared.dto.auth.RefreshTokenBlacklistDto;
import com.shared.dto.auth.UpdateBlacklistTokenRequest;
import com.shared.service.ITokenBlacklistService;
import org.springframework.stereotype.Component;
import com.shared.dto.DeleteStatus;

import java.util.UUID;

@Component("tokenBlacklistController")
public class TokenBlacklistController extends AbstractAuthController implements
        CrudController<CreateBlacklistTokenRequest, RefreshTokenBlacklistDto, UpdateBlacklistTokenRequest, UUID> {

    private final ITokenBlacklistService tokenBlacklistService;

    public TokenBlacklistController(ITokenBlacklistService tokenBlacklistService, ObjectMapper objectMapper) {
        super(objectMapper, "tokenBlacklistController");
        this.tokenBlacklistService = tokenBlacklistService;
    }

    @Override
    public String processCreate(CreateBlacklistTokenRequest request) { return toJson(tokenBlacklistService.blacklistToken(request)); }

    @Override
    public String processGetById(UUID id) { return toJson(tokenBlacklistService.getById(id)); }

    @Override
    public String processGetAll() { return toJson(tokenBlacklistService.getAll()); }

    @Override
    public String processUpdate(UUID id, UpdateBlacklistTokenRequest request) { return toJson(tokenBlacklistService.update(id, request)); }

    @Override
    public String processDelete(UUID id) {
        boolean deleted = tokenBlacklistService.delete(id);
        return toJson(new DeleteStatus(deleted, "Запись blacklist c ID " + id + " обработана"));
    }

    @Override
    public String execute() { return "tokenBlacklistController активен"; }

}