package adrianles.usww.security.authorization;

import adrianles.usww.domain.entity.User;

public interface AuthorizationService {

    boolean canAccessTicket(Integer ticketId);

    boolean canModifyTicket(Integer ticketId);

    boolean canAccessUser(Integer userId);

    boolean canModifyUser(Integer userId);

    boolean canArchiveTicket(Integer ticketId);

    boolean canAccessOrganizationUnit(Integer organizationUnitId);

    boolean isCurrentUserInSameOrganizationAs(User user);

    boolean hasAccessToTicketsByOrganizationUnit(Integer organizationUnitId);

    boolean hasRole(String... roles);
}
