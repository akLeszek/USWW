package adrianles.usww.domain.entity;

import adrianles.usww.domain.entity.dictionary.OrganizationUnit;
import adrianles.usww.domain.entity.dictionary.UserGroup;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class UserTest {

    private Validator validator;
    private User user;
    private UserGroup userGroup;
    private OrganizationUnit organizationUnit;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();

        userGroup = new UserGroup();
        userGroup.setId(1);
        userGroup.setIdn("ADMIN");
        userGroup.setName("Administratorzy");

        organizationUnit = new OrganizationUnit();
        organizationUnit.setId(1);
        organizationUnit.setIdn("IT");
        organizationUnit.setName("Dział IT");

        user = new User();
        user.setId(1);
        user.setLogin("testuser");
        user.setPassword("password123");
        user.setForename("Jan");
        user.setSurname("Kowalski");
        user.setLoginBan(false);
        user.setLastLogin(LocalDateTime.now());
        user.setUserGroup(userGroup);
        user.setOrganizationUnit(organizationUnit);
        user.setArchive(false);
        user.setFirstLogin(true);
    }

    @Test
    @DisplayName("Powinien poprawnie stworzyć obiekt User")
    void shouldCreateUser() {
        assertThat(user).isNotNull();
        assertThat(user.getId()).isEqualTo(1);
        assertThat(user.getLogin()).isEqualTo("testuser");
        assertThat(user.getPassword()).isEqualTo("password123");
        assertThat(user.getForename()).isEqualTo("Jan");
        assertThat(user.getSurname()).isEqualTo("Kowalski");
        assertThat(user.isLoginBan()).isFalse();
        assertThat(user.getLastLogin()).isNotNull();
        assertThat(user.getUserGroup()).isEqualTo(userGroup);
        assertThat(user.getOrganizationUnit()).isEqualTo(organizationUnit);
        assertThat(user.isArchive()).isFalse();
        assertThat(user.isFirstLogin()).isTrue();
    }

    @Test
    @DisplayName("Powinien zwrócić błąd walidacji dla braku wymaganego pola login")
    void shouldFailValidationWhenLoginIsNull() {
        user.setLogin(null);

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertThat(violations).isNotEmpty();
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
                .isEqualTo("Login cannot be null");
    }

    @Test
    @DisplayName("Powinien zwrócić błąd walidacji dla braku wymaganego pola password")
    void shouldFailValidationWhenPasswordIsNull() {
        user.setPassword(null);

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertThat(violations).isNotEmpty();
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
                .isEqualTo("Password cannot be null");
    }

    @Test
    @DisplayName("Powinien poprawnie przejść walidację dla wszystkich wymaganych pól")
    void shouldPassValidationWithAllRequiredFields() {
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("Powinien poprawnie wykorzystać konstruktory")
    void shouldUseConstructors() {
        // given
        User emptyUser = new User();

        User fullUser = new User(
                "testuser2",
                "password456",
                "Anna",
                "Nowak",
                false,
                LocalDateTime.now(),
                userGroup,
                organizationUnit,
                false,
                true
        );

        fullUser.setId(2);

        assertThat(emptyUser).isNotNull();

        assertThat(fullUser).isNotNull();
        assertThat(fullUser.getId()).isEqualTo(2);
        assertThat(fullUser.getLogin()).isEqualTo("testuser2");
        assertThat(fullUser.getPassword()).isEqualTo("password456");
        assertThat(fullUser.getForename()).isEqualTo("Anna");
        assertThat(fullUser.getSurname()).isEqualTo("Nowak");
        assertThat(fullUser.isLoginBan()).isFalse();
        assertThat(fullUser.getLastLogin()).isNotNull();
        assertThat(fullUser.getUserGroup()).isEqualTo(userGroup);
        assertThat(fullUser.getOrganizationUnit()).isEqualTo(organizationUnit);
        assertThat(fullUser.isArchive()).isFalse();
        assertThat(fullUser.isFirstLogin()).isTrue();
    }

    @Test
    @DisplayName("Powinien poprawnie porównać dwa identyczne obiekty")
    void shouldCompareEqualObjects() {
        User user2 = new User();
        user2.setId(1);
        user2.setLogin("testuser");
        user2.setPassword("password123");
        user2.setForename("Jan");
        user2.setSurname("Kowalski");
        user2.setLoginBan(false);
        user2.setLastLogin(user.getLastLogin());
        user2.setUserGroup(userGroup);
        user2.setOrganizationUnit(organizationUnit);
        user2.setArchive(false);
        user2.setFirstLogin(true);

        assertThat(user).isEqualTo(user);
        assertThat(user.hashCode()).isEqualTo(user.hashCode());

        assertThat(user).isEqualTo(user2);
        assertThat(user.hashCode()).isEqualTo(user2.hashCode());
    }

    @Test
    @DisplayName("Powinien poprawnie rozróżnić dwa różne obiekty")
    void shouldDifferentiateNonEqualObjects() {
        User user2 = new User();
        user2.setId(2); // Różne ID
        user2.setLogin("testuser");
        user2.setPassword("password123");
        user2.setForename("Jan");
        user2.setSurname("Kowalski");
        user2.setLoginBan(false);
        user2.setLastLogin(user.getLastLogin());
        user2.setUserGroup(userGroup);
        user2.setOrganizationUnit(organizationUnit);
        user2.setArchive(false);
        user2.setFirstLogin(true);

        assertThat(user).isNotEqualTo(null);
        assertThat(user).isNotEqualTo(new Object());
        assertThat(user).isNotEqualTo(user2);
        assertThat(user.hashCode()).isNotEqualTo(user2.hashCode());
    }
}
