package adrianles.usww.api.controller.admin;

import adrianles.usww.api.dto.UserDTO;
import adrianles.usww.service.facade.UserService;
import adrianles.usww.service.facade.UserStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ADMIN')")
public class AdminUserController {

    private final UserService userService;
    private final UserStatusService userStatusService;

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
        return ResponseEntity.ok(userStatusService.blockUser(id));
    }

    @PostMapping("/{id}/unblock")
    public ResponseEntity<UserDTO> unblockUser(@PathVariable int id) {
        return ResponseEntity.ok(userStatusService.unblockUser(id));
    }

    @PostMapping("/{id}/archive")
    public ResponseEntity<UserDTO> archiveUser(@PathVariable int id) {
        return ResponseEntity.ok(userStatusService.archiveUser(id));
    }

    @GetMapping("/archived")
    public ResponseEntity<List<UserDTO>> getArchivedUsers() {
        return ResponseEntity.ok(userStatusService.getArchivedUsers());
    }

    @PostMapping("/{id}/restore")
    public ResponseEntity<UserDTO> restoreUser(@PathVariable int id) {
        return ResponseEntity.ok(userStatusService.restoreUser(id));
    }

    @GetMapping("/operators")
    public ResponseEntity<List<UserDTO>> getOperators() {
        return ResponseEntity.ok(userService.getAllOperators());
    }

    @GetMapping("/operators/by-student/{studentId}")
    public ResponseEntity<List<UserDTO>> getOperatorsBySameOrganizationAsStudent(@PathVariable int studentId) {
        return ResponseEntity.ok(userService.getOperatorsBySameOrganizationUnitAsStudent(studentId));
    }
}
