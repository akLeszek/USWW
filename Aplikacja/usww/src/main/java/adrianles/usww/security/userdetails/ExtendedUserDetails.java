package adrianles.usww.security.userdetails;

import org.springframework.security.core.userdetails.UserDetails;

public interface ExtendedUserDetails extends UserDetails {

    boolean isFirstLogin();

    Integer getUserId();

    boolean isAdmin();

    boolean isOperator();

    boolean isStudent();
}
