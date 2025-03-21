package adrianles.usww.unit.security;

import adrianles.usww.domain.entity.User;
import adrianles.usww.domain.entity.dictionary.UserGroup;
import adrianles.usww.security.userdetails.CustomUserDetails;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

class CustomUserDetailsTest {

    private User user;
    private CustomUserDetails customUserDetails;

    @BeforeEach
    void setUp() {
        UserGroup adminGroup = new UserGroup();
        adminGroup.setId(1);
        adminGroup.setIdn("ADMIN");

        user = new User();
        user.setId(1);
        user.setLogin("admin_user");
        user.setPassword("encodedPassword");
        user.setLoginBan(false);
        user.setArchive(false);
        user.setUserGroup(adminGroup);
        user.setFirstLogin(false);

        customUserDetails = new CustomUserDetails(user);
    }

    @Test
    @DisplayName("Powinien poprawnie zwrócić uprawnienia użytkownika")
    void shouldReturnAuthorities() {
        Collection<? extends GrantedAuthority> authorities = customUserDetails.getAuthorities();

        assertThat(authorities).hasSize(1);
        assertThat(authorities.iterator().next().getAuthority()).isEqualTo("ADMIN");
    }

    @Test
    @DisplayName("Powinien poprawnie zwrócić hasło użytkownika")
    void shouldReturnPassword() {
        assertThat(customUserDetails.getPassword()).isEqualTo("encodedPassword");
    }

    @Test
    @DisplayName("Powinien poprawnie zwrócić nazwę użytkownika")
    void shouldReturnUsername() {
        assertThat(customUserDetails.getUsername()).isEqualTo("admin_user");
    }

    @Test
    @DisplayName("Powinien poprawnie sprawdzić aktywność konta")
    void shouldCheckAccountStatus() {
        assertThat(customUserDetails.isAccountNonExpired()).isTrue();
        assertThat(customUserDetails.isAccountNonLocked()).isTrue();
        assertThat(customUserDetails.isCredentialsNonExpired()).isTrue();
        assertThat(customUserDetails.isEnabled()).isTrue();
    }

    @Test
    @DisplayName("Powinien poprawnie obsłużyć pierwszy login")
    void shouldHandleFirstLogin() {
        user.setFirstLogin(true);
        customUserDetails = new CustomUserDetails(user);
        assertThat(customUserDetails.isFirstLogin()).isTrue();
    }

    @Test
    @DisplayName("Powinien poprawnie zwrócić ID użytkownika")
    void shouldReturnUserId() {
        assertThat(customUserDetails.getUserId()).isEqualTo(1);
    }

    @Test
    @DisplayName("Powinien poprawnie sprawdzić role użytkownika")
    void shouldCheckUserRoles() {
        assertThat(customUserDetails.isAdmin()).isTrue();
        assertThat(customUserDetails.isOperator()).isFalse();
        assertThat(customUserDetails.isStudent()).isFalse();
    }

    @Test
    @DisplayName("Powinien poprawnie obsłużyć zablokowane konto")
    void shouldHandleLockedAccount() {
        user.setLoginBan(true);
        customUserDetails = new CustomUserDetails(user);
        assertThat(customUserDetails.isAccountNonLocked()).isFalse();
    }

    @Test
    @DisplayName("Powinien poprawnie obsłużyć zarchiwizowane konto")
    void shouldHandleArchivedAccount() {
        user.setArchive(true);
        customUserDetails = new CustomUserDetails(user);
        assertThat(customUserDetails.isEnabled()).isFalse();
    }
}
