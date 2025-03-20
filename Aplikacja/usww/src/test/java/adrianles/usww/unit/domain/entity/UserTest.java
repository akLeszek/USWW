package adrianles.usww.unit.domain.entity;

import adrianles.usww.domain.entity.User;
import adrianles.usww.domain.entity.dictionary.OrganizationUnit;
import adrianles.usww.domain.entity.dictionary.UserGroup;
import adrianles.usww.utils.UserGroupUtils;
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
    @DisplayName("Powinien zwrócić błąd walidacji dla zbyt długiego loginu")
    void shouldFailValidationWhenLoginIsTooLong() {
        user.setLogin("a".repeat(33)); // 33 znaki, max to 32

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertThat(violations).isNotEmpty();
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
                .contains("Login cannot exceed 32 characters");
    }

    @Test
    @DisplayName("Powinien zwrócić błąd walidacji dla zbyt długiego imienia")
    void shouldFailValidationWhenForenameIsTooLong() {
        user.setForename("a".repeat(33)); // 33 znaki, max to 32

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertThat(violations).isNotEmpty();
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
                .contains("Forename cannot exceed 32 characters");
    }

    @Test
    @DisplayName("Powinien zwrócić błąd walidacji dla zbyt długiego nazwiska")
    void shouldFailValidationWhenSurnameIsTooLong() {
        user.setSurname("a".repeat(65)); // 65 znaków, max to 64

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertThat(violations).isNotEmpty();
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
                .contains("Surname cannot exceed 64 characters");
    }

    @Test
    @DisplayName("Powinien poprawnie przejść walidację dla wszystkich wymaganych pól")
    void shouldPassValidationWithAllRequiredFields() {
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("Powinien poprawnie przejść walidację dla maksymalnych długości pól")
    void shouldPassValidationWithMaxLengthFields() {
        user.setLogin("a".repeat(32));
        user.setForename("b".repeat(32));
        user.setSurname("c".repeat(64));

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("Powinien poprawnie wykorzystać konstruktory")
    void shouldUseConstructors() {
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

    @Test
    @DisplayName("Powinien prawidłowo zwalidować relację z UserGroup")
    void shouldValidateUserGroupRelation() {
        // test usunięcia wymaganej grupy
        user.setUserGroup(null);

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertThat(violations).isNotEmpty();

        // przywrócenie grupy
        user.setUserGroup(userGroup);
        violations = validator.validate(user);
        assertThat(violations).isEmpty();

        // sprawdzenie właściwości grupy
        assertThat(user.getUserGroup().getIdn()).isEqualTo("ADMIN");
        assertThat(user.getUserGroup().getName()).isEqualTo("Administratorzy");
    }

    @Test
    @DisplayName("Powinien prawidłowo obsługiwać relację z OrganizationUnit")
    void shouldHandleOrganizationUnitRelation() {
        // organizationUnit nie jest wymagane, więc można je usunąć
        user.setOrganizationUnit(null);
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertThat(violations).isEmpty();

        // przywrócenie organizationUnit
        user.setOrganizationUnit(organizationUnit);
        assertThat(user.getOrganizationUnit()).isEqualTo(organizationUnit);

        // sprawdzenie właściwości jednostki organizacyjnej
        assertThat(user.getOrganizationUnit().getIdn()).isEqualTo("IT");
        assertThat(user.getOrganizationUnit().getName()).isEqualTo("Dział IT");
    }

    @Test
    @DisplayName("Powinien obsługiwać funkcje warunków dotyczących grupy użytkownika")
    void shouldHandleUserGroupConditions() {
        // Test dla grupy ADMIN
        assertThat(UserGroupUtils.isAdmin(user.getUserGroup())).isTrue();
        assertThat(UserGroupUtils.isOperator(user.getUserGroup())).isFalse();
        assertThat(UserGroupUtils.isStudent(user.getUserGroup())).isFalse();

        // Zmiana grupy na TECH (operator)
        UserGroup techGroup = new UserGroup();
        techGroup.setId(2);
        techGroup.setIdn("OPERATOR");
        techGroup.setName("Operatorzy");
        user.setUserGroup(techGroup);

        assertThat(UserGroupUtils.isAdmin(user.getUserGroup())).isFalse();
        assertThat(UserGroupUtils.isOperator(user.getUserGroup())).isTrue();
        assertThat(UserGroupUtils.isStudent(user.getUserGroup())).isFalse();

        // Zmiana grupy na STUDENT
        UserGroup studentGroup = new UserGroup();
        studentGroup.setId(3);
        studentGroup.setIdn("STUDENT");
        studentGroup.setName("Studenci");
        user.setUserGroup(studentGroup);

        assertThat(UserGroupUtils.isAdmin(user.getUserGroup())).isFalse();
        assertThat(UserGroupUtils.isOperator(user.getUserGroup())).isFalse();
        assertThat(UserGroupUtils.isStudent(user.getUserGroup())).isTrue();
    }
}
