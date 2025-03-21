package adrianles.usww.unit.utils;

import adrianles.usww.domain.entity.dictionary.UserGroup;
import adrianles.usww.utils.UserGroupUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

public class UserGroupUtilsTest {

    @Test
    @DisplayName("isAdmin powinien zwrócić true dla grupy ADMIN")
    void isAdmin_shouldReturnTrueForAdminGroup() {
        // given
        UserGroup adminGroup = createUserGroup("ADMIN");

        // when
        boolean result = UserGroupUtils.isAdmin(adminGroup);

        // then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("isAdmin powinien zwrócić false dla grupy nie będącej ADMIN")
    void isAdmin_shouldReturnFalseForNonAdminGroup() {
        // given
        UserGroup operatorGroup = createUserGroup("OPERATOR");

        // when
        boolean result = UserGroupUtils.isAdmin(operatorGroup);

        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("isAdmin powinien zwrócić false dla null")
    void isAdmin_shouldReturnFalseForNull() {
        // when
        boolean result = UserGroupUtils.isAdmin(null);

        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("isOperator powinien zwrócić true dla grupy OPERATOR")
    void isOperator_shouldReturnTrueForOperatorGroup() {
        // given
        UserGroup operatorGroup = createUserGroup("OPERATOR");

        // when
        boolean result = UserGroupUtils.isOperator(operatorGroup);

        // then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("isOperator powinien zwrócić false dla grupy nie będącej OPERATOR")
    void isOperator_shouldReturnFalseForNonOperatorGroup() {
        // given
        UserGroup adminGroup = createUserGroup("ADMIN");

        // when
        boolean result = UserGroupUtils.isOperator(adminGroup);

        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("isOperator powinien zwrócić false dla null")
    void isOperator_shouldReturnFalseForNull() {
        // when
        boolean result = UserGroupUtils.isOperator(null);

        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("isStudent powinien zwrócić true dla grupy STUDENT")
    void isStudent_shouldReturnTrueForStudentGroup() {
        // given
        UserGroup studentGroup = createUserGroup("STUDENT");

        // when
        boolean result = UserGroupUtils.isStudent(studentGroup);

        // then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("isStudent powinien zwrócić false dla grupy nie będącej STUDENT")
    void isStudent_shouldReturnFalseForNonStudentGroup() {
        // given
        UserGroup adminGroup = createUserGroup("ADMIN");

        // when
        boolean result = UserGroupUtils.isStudent(adminGroup);

        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("isStudent powinien zwrócić false dla null")
    void isStudent_shouldReturnFalseForNull() {
        // when
        boolean result = UserGroupUtils.isStudent(null);

        // then
        assertThat(result).isFalse();
    }

    @ParameterizedTest
    @DisplayName("requiresOrganizationUnit powinien zwrócić true dla wszystkich grup oprócz ADMIN")
    @ValueSource(strings = {"OPERATOR", "STUDENT", "OTHER"})
    void requiresOrganizationUnit_shouldReturnTrueForNonAdminGroups(String groupIdn) {
        // given
        UserGroup userGroup = createUserGroup(groupIdn);

        // when
        boolean result = UserGroupUtils.requiresOrganizationUnit(userGroup);

        // then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("requiresOrganizationUnit powinien zwrócić false dla grupy ADMIN")
    void requiresOrganizationUnit_shouldReturnFalseForAdminGroup() {
        // given
        UserGroup adminGroup = createUserGroup("ADMIN");

        // when
        boolean result = UserGroupUtils.requiresOrganizationUnit(adminGroup);

        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("requiresOrganizationUnit powinien zwrócić false dla null")
    void requiresOrganizationUnit_shouldReturnFalseForNull() {
        // when
        boolean result = UserGroupUtils.requiresOrganizationUnit(null);

        // then
        assertThat(result).isFalse();
    }

    private UserGroup createUserGroup(String idn) {
        UserGroup userGroup = new UserGroup();
        userGroup.setIdn(idn);
        return userGroup;
    }
}
