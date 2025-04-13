package adrianles.usww.api.controller.common;

import adrianles.usww.api.dto.UserDTO;
import adrianles.usww.service.facade.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/profile")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserDTO> getCurrentUserProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        return ResponseEntity.ok(userService.getUserByLogin(currentUsername));
    }

    @GetMapping("/{id}/basic-info")
    @PreAuthorize("isAuthenticated() and (hasAuthority('ADMIN') or hasPermission(#id, 'User', 'READ'))")
    public ResponseEntity<UserDTO> getUserBasicInfo(@PathVariable int id) {
        return ResponseEntity.ok(userService.getUserBasicInfo(id));
    }

    @GetMapping("/by-login/{login}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserDTO> getUserByLogin(@PathVariable String login) {
        return ResponseEntity.ok(userService.getUserByLogin(login));
    }
}
