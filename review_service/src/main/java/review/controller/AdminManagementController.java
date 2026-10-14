package review.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/management")
@RequiredArgsConstructor
public class AdminManagementController {

    @DeleteMapping("/purge-data")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> purgeSystemData() {
        return ResponseEntity.noContent().build();
    }
}