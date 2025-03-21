package adrianles.usww.unit.api.mapper;

import adrianles.usww.api.dto.UserDTO;
import adrianles.usww.api.mapper.UserMapper;
import adrianles.usww.domain.entity.User;
import adrianles.usww.domain.entity.dictionary.OrganizationUnit;
import adrianles.usww.domain.entity.dictionary.UserGroup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

public class UserMapperTest {

    private UserMapper userMapper;
    private User user;

    @BeforeEach
    void setUp() {
        userMapper = new UserMapper();

        UserGroup userGroup = new UserGroup();
        userGroup.setId(1);
        userGroup.setIdn("ADMIN");
        userGroup.setName("Administratorzy");

        OrganizationUnit organizationUnit = new OrganizationUnit();
        organizationUnit.setId(2);
        organizationUnit.setIdn("IT");
        organizationUnit.setName("Dział IT");

        user = new User();
        user.setId(1);
        user.setLogin("testuser");
        user.setPassword("encodedPassword");
        user.setForename("Jan");
        user.setSurname("Kowalski");
        user.setLoginBan(false);
        user.setLastLogin(LocalDateTime.of(2024, 2, 16, 10, 0));
        user.setUserGroup(userGroup);
        user.setOrganizationUnit(organizationUnit);
        user.setArchive(false);
        user.setFirstLogin(true);
    }

    @Test
    @DisplayName("toDto powinien poprawnie mapować pełny obiekt User na UserDTO")
    void toDto_shouldMapFullUserToUserDTO() {
        // When
        UserDTO userDTO = userMapper.toDto(user);

        // Then
        assertThat(userDTO).isNotNull();
        assertThat(userDTO.getId()).isEqualTo(1);
        assertThat(userDTO.getLogin()).isEqualTo("testuser");
        assertThat(userDTO.getForename()).isEqualTo("Jan");
        assertThat(userDTO.getSurname()).isEqualTo("Kowalski");
        assertThat(userDTO.isLoginBan()).isFalse();
        assertThat(userDTO.getLastLogin()).isEqualTo("2024-02-16T10:00");
        assertThat(userDTO.getGroupId()).isEqualTo(1);
        assertThat(userDTO.getOrganizationUnitId()).isEqualTo(2);
        assertThat(userDTO.isArchive()).isFalse();
        assertThat(userDTO.isFirstLogin()).isTrue();
    }

    @Test
    @DisplayName("toDto powinien obsłużyć null w obiekcie User")
    void toDto_shouldHandleNullUser() {
        // When
        UserDTO userDTO = userMapper.toDto(null);

        // Then
        assertThat(userDTO).isNull();
    }

    @Test
    @DisplayName("toDto powinien obsłużyć użytkownika bez jednostki organizacyjnej")
    void toDto_shouldHandleUserWithoutOrganizationUnit() {
        // Given
        user.setOrganizationUnit(null);

        // When
        UserDTO userDTO = userMapper.toDto(user);

        // Then
        assertThat(userDTO).isNotNull();
        assertThat(userDTO.getOrganizationUnitId()).isNull();
    }

    @Test
    @DisplayName("toBasicInfoDto powinien zwrócić podstawowe informacje o użytkowniku")
    void toBasicInfoDto_shouldReturnBasicUserInfo() {
        // When
        UserDTO basicInfoDTO = userMapper.toBasicInfoDto(user);

        // Then
        assertThat(basicInfoDTO).isNotNull();
        assertThat(basicInfoDTO.getId()).isEqualTo(1);
        assertThat(basicInfoDTO.getForename()).isEqualTo("Jan");
        assertThat(basicInfoDTO.getSurname()).isEqualTo("Kowalski");
        assertThat(basicInfoDTO.getLogin()).isNull();
        assertThat(basicInfoDTO.getGroupId()).isNull();
        assertThat(basicInfoDTO.getOrganizationUnitId()).isNull();
    }

    @Test
    @DisplayName("toBasicInfoDto powinien obsłużyć null w obiekcie User")
    void toBasicInfoDto_shouldHandleNullUser() {
        // When
        UserDTO basicInfoDTO = userMapper.toBasicInfoDto(null);

        // Then
        assertThat(basicInfoDTO).isNull();
    }
}
