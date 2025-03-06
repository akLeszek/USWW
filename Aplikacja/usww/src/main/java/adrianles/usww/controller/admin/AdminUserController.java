package adrianles.usww.controller.admin;

import adrianles.usww.dto.UserDTO;
import adrianles.usww.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
public class AdminUserController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable int id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @PostMapping
    public ResponseEntity<UserDTO> createUser(@RequestBody UserDTO userDTO) {
        return ResponseEntity.ok(userService.createUser(userDTO));
    }

    @PostMapping("/{id}/block")
    public ResponseEntity<UserDTO> blockUser(@PathVariable int id) {
        return ResponseEntity.ok(userService.blockUser(id));
    }

    @PostMapping("/{id}/unblock")
    public ResponseEntity<UserDTO> unblockUser(@PathVariable int id) {
        return ResponseEntity.ok(userService.unblockUser(id));
    }

    @PostMapping("/{id}/archive")
    public ResponseEntity<UserDTO> archiveUser(@PathVariable int id) {
        return ResponseEntity.ok(userService.archiveUser(id));
    }

    @GetMapping("/archived")
    public ResponseEntity<List<UserDTO>> getArchivedUsers() {
        return ResponseEntity.ok(userService.getArchivedUsers());
    }

    @PostMapping("/{id}/restore")
    public ResponseEntity<UserDTO> restoreUser(@PathVariable int id) {
        return ResponseEntity.ok(userService.restoreUser(id));
    }
}
