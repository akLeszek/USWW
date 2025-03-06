package adrianles.usww.controller;

import adrianles.usww.configuration.jwtAuthentication.AuthRequest;
import adrianles.usww.configuration.jwtAuthentication.AuthResponse;
import adrianles.usww.configuration.jwtAuthentication.JwtUtil;
import adrianles.usww.dto.PasswordChangeRequestDTO;
import adrianles.usww.dto.UserDTO;
import adrianles.usww.entity.User;
import adrianles.usww.repository.UserRepository;
import adrianles.usww.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest authRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDetails userDetails = userDetailsService.loadUserByUsername(authRequest.getUsername());

        Optional<User> optionalUser = userRepository.findByLogin(authRequest.getUsername());
        if (optionalUser.isPresent() && optionalUser.get().isFirstLogin()) {
            return userFirstLogin(optionalUser.get(), userDetails.getUsername());
        }

        String token = jwtUtil.generateToken(userDetails.getUsername());
        return ResponseEntity.ok(new AuthResponse(token));
    }

    private ResponseEntity<?> userFirstLogin(User user, String username) {
        Map<String, Object> response = new HashMap<>();
        response.put("requirePasswordChange", true);
        response.put("token", jwtUtil.generateToken(username));
        response.put("userId", user.getId());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(
            @RequestParam("userId") Integer userId,
            @Valid @RequestBody PasswordChangeRequestDTO request) {
        try {
            return changeUserPassword(userId, request);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    private ResponseEntity<?> changeUserPassword(Integer userId, PasswordChangeRequestDTO request) {
        UserDTO updatedUser = userService.changePassword(userId, request.getCurrentPassword(), request.getNewPassword());
        UserDetails userDetails = userDetailsService.loadUserByUsername(updatedUser.getLogin());
        String token = jwtUtil.generateToken(userDetails.getUsername());

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Password changed successfully");
        response.put("token", token);
        return ResponseEntity.ok(response);
    }
}
