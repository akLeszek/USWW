package adrianles.usww.security.authorization;

import adrianles.usww.domain.entity.Ticket;
import adrianles.usww.domain.entity.User;
import adrianles.usww.domain.repository.TicketRepository;
import adrianles.usww.domain.repository.UserRepository;
import adrianles.usww.exception.ResourceNotFoundException;
import adrianles.usww.security.userdetails.ExtendedUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthorizationServiceImpl implements AuthorizationService {

    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;

    @Override
    public boolean canAccessTicket(Integer ticketId) {
        if (hasRole("ADMIN")) {
            return true;
        }

        ExtendedUserDetails userDetails = getCurrentUserDetails();
        if (userDetails == null) {
            return false;
        }

        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found with id: " + ticketId));

        if (hasRole("OPERATOR")) {
            Integer operatorId = ticket.getOperator() != null ? ticket.getOperator().getId() : null;
            return userDetails.getUserId().equals(operatorId) ||
                    isSameOrganizationUnit(userDetails.getUserId(), ticket.getStudent().getId());
        }

        return userDetails.getUserId().equals(ticket.getStudent().getId());
    }

    @Override
    public boolean canModifyTicket(Integer ticketId) {
        if (hasRole("ADMIN")) {
            return true;
        }

        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found with id: " + ticketId));

        if (ticket.isArchive()) {
            return false;
        }

        ExtendedUserDetails userDetails = getCurrentUserDetails();
        if (userDetails == null) {
            return false;
        }

        if (hasRole("OPERATOR")) {
            Integer operatorId = ticket.getOperator() != null ? ticket.getOperator().getId() : null;
            return userDetails.getUserId().equals(operatorId);
        }

        return false;
    }

    @Override
    public boolean canAccessUser(Integer userId) {
        if (hasRole("ADMIN")) {
            return true;
        }

        ExtendedUserDetails userDetails = getCurrentUserDetails();
        if (userDetails == null) {
            return false;
        }

        if (userDetails.getUserId().equals(userId)) {
            return true;
        }

        return hasRole("OPERATOR") && isSameOrganizationUnit(userDetails.getUserId(), userId);
    }

    @Override
    public boolean canModifyUser(Integer userId) {
        if (hasRole("ADMIN")) {
            return true;
        }

        ExtendedUserDetails userDetails = getCurrentUserDetails();
        return userDetails != null && userDetails.getUserId().equals(userId);
    }

    @Override
    public boolean canArchiveTicket(Integer ticketId) {
        return hasRole("ADMIN", "OPERATOR") && canAccessTicket(ticketId);
    }

    @Override
    public boolean canAccessOrganizationUnit(Integer organizationUnitId) {
        if (hasRole("ADMIN")) {
            return true;
        }

        ExtendedUserDetails userDetails = getCurrentUserDetails();
        if (userDetails == null) {
            return false;
        }

        User user = userRepository.findById(userDetails.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return user.getOrganizationUnit() != null &&
                user.getOrganizationUnit().getId().equals(organizationUnitId);
    }

    @Override
    public boolean isCurrentUserInSameOrganizationAs(User user) {
        ExtendedUserDetails userDetails = getCurrentUserDetails();
        if (userDetails == null || user == null) {
            return false;
        }

        return isSameOrganizationUnit(userDetails.getUserId(), user.getId());
    }

    @Override
    public boolean hasAccessToTicketsByOrganizationUnit(Integer organizationUnitId) {
        if (hasRole("ADMIN")) {
            return true;
        }

        return canAccessOrganizationUnit(organizationUnitId);
    }

    @Override
    public boolean hasRole(String... roles) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        for (String role : roles) {
            if (authentication.getAuthorities().contains(new SimpleGrantedAuthority(role))) {
                return true;
            }
        }

        return false;
    }

    private ExtendedUserDetails getCurrentUserDetails() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof ExtendedUserDetails) {
            return (ExtendedUserDetails) principal;
        }

        return null;
    }

    private boolean isSameOrganizationUnit(Integer userId1, Integer userId2) {
        User user1 = userRepository.findById(userId1)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        User user2 = userRepository.findById(userId2)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (user1.getOrganizationUnit() == null || user2.getOrganizationUnit() == null) {
            return false;
        }

        return user1.getOrganizationUnit().getId().equals(user2.getOrganizationUnit().getId());
    }
}
