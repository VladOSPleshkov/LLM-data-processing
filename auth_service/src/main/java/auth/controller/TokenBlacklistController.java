package auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shared.CrudController;
import com.shared.dto.auth.CreateBlacklistTokenRequest;
import com.shared.dto.auth.RefreshTokenBlacklistDto;
import com.shared.dto.auth.UpdateBlacklistTokenRequest;
import com.shared.service.ITokenBlacklistService;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import com.shared.dto.DeleteStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/tokenblacklist")
public class TokenBlacklistController {

    private final ITokenBlacklistService tokenBlacklistService;

    public TokenBlacklistController(ITokenBlacklistService tokenBlacklistService) {
        this.tokenBlacklistService = tokenBlacklistService;
    }

    @PostMapping
    public ResponseEntity<RefreshTokenBlacklistDto> processCreate(@RequestBody CreateBlacklistTokenRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(tokenBlacklistService.blacklistToken(request)); }

    @GetMapping("/{id}")
    public ResponseEntity<RefreshTokenBlacklistDto> processGetById(UUID id) {
        return ResponseEntity.ok(tokenBlacklistService.getById(id)); }

    @GetMapping
    public ResponseEntity<List<RefreshTokenBlacklistDto>>processGetAll() {
        return ResponseEntity.ok(tokenBlacklistService.getAll()); }

    @PutMapping("/{id}")
    public ResponseEntity<RefreshTokenBlacklistDto> processUpdate(UUID id, UpdateBlacklistTokenRequest request) {
        return ResponseEntity.ok(tokenBlacklistService.update(id, request)); }

    @DeleteMapping("/{id}")
    public ResponseEntity<RefreshTokenBlacklistDto> processDelete(UUID id) {
        tokenBlacklistService.delete(id);
        return ResponseEntity.noContent().build();
    }
}