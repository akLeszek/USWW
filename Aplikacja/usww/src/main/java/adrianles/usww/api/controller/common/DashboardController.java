package adrianles.usww.api.controller.common;

import adrianles.usww.api.dto.TicketDTO;
import adrianles.usww.api.dto.UserDTO;
import adrianles.usww.security.userdetails.ExtendedUserDetails;
import adrianles.usww.service.facade.TicketService;
import adrianles.usww.service.facade.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {
    private final TicketService ticketService;
    private final UserService userService;

    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Integer>> getStatistics() {
        UserDetails userDetails = getCurrentUserDetails();
        Integer userId = extractUserId(userDetails);
        boolean isAdmin = hasRole(userDetails, "ADMIN");
        boolean isOperator = hasRole(userDetails, "OPERATOR");

        Map<String, Integer> statistics = new HashMap<>();
        statistics.put("activeTickets", ticketService.countActiveTicketsByUser(userId, isAdmin, isOperator));
        statistics.put("closedTickets", ticketService.countClosedTicketsByUser(userId, isAdmin, isOperator));
        statistics.put("pendingTickets", ticketService.countPendingTicketsByUser(userId, isAdmin, isOperator));
        statistics.put("totalTickets", ticketService.countAllTicketsByUser(userId, isAdmin, isOperator));

        return ResponseEntity.ok(statistics);
    }

    @GetMapping("/tickets/recent")
    public ResponseEntity<List<TicketDTO>> getRecentTickets(
            @RequestParam(defaultValue = "5") int limit) {
        UserDetails userDetails = getCurrentUserDetails();
        Integer userId = extractUserId(userDetails);
        boolean isAdmin = hasRole(userDetails, "ADMIN");
        boolean isOperator = hasRole(userDetails, "OPERATOR");

        List<TicketDTO> recentTickets = ticketService.getRecentTicketsByUser(userId, isAdmin, isOperator, limit);
        return ResponseEntity.ok(recentTickets);
    }

    private UserDetails getCurrentUserDetails() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AccessDeniedException("User not authenticated");
        }
        return (UserDetails) authentication.getPrincipal();
    }

    private Integer extractUserId(UserDetails userDetails) {
        if (userDetails instanceof ExtendedUserDetails) {
            return ((ExtendedUserDetails) userDetails).getUserId();
        }

        String username = userDetails.getUsername();
        UserDTO user = userService.getUserByLogin(username);
        return user.getId();
    }

    private boolean hasRole(UserDetails userDetails, String roleName) {
        return userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals(roleName));
    }
}
