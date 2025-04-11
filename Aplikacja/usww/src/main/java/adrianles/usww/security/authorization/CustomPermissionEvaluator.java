package adrianles.usww.security.authorization;

import adrianles.usww.domain.entity.Ticket;
import adrianles.usww.domain.entity.User;
import adrianles.usww.domain.repository.TicketRepository;
import adrianles.usww.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CustomPermissionEvaluator implements PermissionEvaluator {

    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;
    private final AuthorizationService authorizationService;

    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
        if (authentication == null || targetDomainObject == null || !(permission instanceof String)) {
            return false;
        }

        if (targetDomainObject instanceof Ticket) {
            return hasTicketPermission(authentication, (Ticket) targetDomainObject, (String) permission);
        }

        if (targetDomainObject instanceof User) {
            return hasUserPermission(authentication, (User) targetDomainObject, (String) permission);
        }

        return false;
    }

    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {
        if (authentication == null || targetId == null || !(targetId instanceof Integer) || targetType == null || !(permission instanceof String)) {
            return false;
        }

        Integer id = (Integer) targetId;

        if ("Ticket".equals(targetType)) {
            return hasTicketPermissionById(authentication, id, (String) permission);
        }

        if ("User".equals(targetType)) {
            return hasUserPermissionById(authentication, id, (String) permission);
        }

        return false;
    }

    private boolean hasTicketPermission(Authentication authentication, Ticket ticket, String permission) {
        switch (permission) {
            case "READ":
                return authorizationService.canAccessTicket(ticket.getId());
            case "WRITE":
                return authorizationService.canModifyTicket(ticket.getId());
            case "ARCHIVE":
                return authorizationService.canArchiveTicket(ticket.getId());
            default:
                return false;
        }
    }

    private boolean hasTicketPermissionById(Authentication authentication, Integer ticketId, String permission) {
        Optional<Ticket> ticket = ticketRepository.findById(ticketId);
        if (ticket.isEmpty()) {
            return false;
        }
        return hasTicketPermission(authentication, ticket.get(), permission);
    }

    private boolean hasUserPermission(Authentication authentication, User user, String permission) {
        switch (permission) {
            case "READ":
                return authorizationService.canAccessUser(user.getId());
            case "WRITE":
                return authorizationService.canModifyUser(user.getId());
            default:
                return false;
        }
    }

    private boolean hasUserPermissionById(Authentication authentication, Integer userId, String permission) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            return false;
        }
        return hasUserPermission(authentication, user.get(), permission);
    }
}
