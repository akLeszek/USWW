package adrianles.usww.unit.api.mapper.dictionary;

import adrianles.usww.api.dto.dictionary.UserGroupDTO;
import adrianles.usww.api.mapper.dictionary.UserGroupMapper;
import adrianles.usww.domain.entity.dictionary.UserGroup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

class UserGroupMapperTest {

    private UserGroupMapper userGroupMapper;
    private UserGroup adminGroup;
    private UserGroup operatorGroup;

    @BeforeEach
    void setUp() {
        userGroupMapper = new UserGroupMapper();

        adminGroup = new UserGroup();
        adminGroup.setId(1);
        adminGroup.setIdn("ADMIN");
        adminGroup.setName("Administratorzy");

        operatorGroup = new UserGroup();
        operatorGroup.setId(2);
        operatorGroup.setIdn("OPERATOR");
        operatorGroup.setName("Operatorzy");
    }

    @Test
    @DisplayName("Powinien poprawnie mapować encję UserGroup na DTO")
    void toDto_shouldMapEntityToDto() {
        // when
        UserGroupDTO result = userGroupMapper.toDto(adminGroup);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1);
        assertThat(result.getIdn()).isEqualTo("ADMIN");
        assertThat(result.getName()).isEqualTo("Administratorzy");
    }

    @Test
    @DisplayName("Powinien zwrócić null gdy encja jest null")
    void toDto_shouldReturnNullWhenEntityIsNull() {
        // when
        UserGroupDTO result = userGroupMapper.toDto(null);

        // then
        assertThat(result).isNull();
    }

    @Test
    @DisplayName("Powinien poprawnie mapować encję z polami null")
    void toDto_shouldMapEntityWithNullFields() {
        // given
        UserGroup groupWithNulls = new UserGroup();
        groupWithNulls.setId(3);
        groupWithNulls.setIdn(null);
        groupWithNulls.setName(null);

        // when
        UserGroupDTO result = userGroupMapper.toDto(groupWithNulls);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(3);
        assertThat(result.getIdn()).isNull();
        assertThat(result.getName()).isNull();
    }

    @ParameterizedTest
    @DisplayName("Powinien poprawnie mapować pole requiresOrganizationUnit")
    @CsvSource({
            "ADMIN, false",
            "OPERATOR, true",
            "STUDENT, true"
    })
    void mapSpecificFields_shouldSetRequiresOrganizationUnitCorrectly(String groupIdn, boolean expectedValue) {
        // given
        UserGroup group = new UserGroup();
        group.setId(1);
        group.setIdn(groupIdn);
        group.setName("Test Group");

        // when
        UserGroupDTO result = userGroupMapper.toDto(group);

        // then
        assertThat(result).isNotNull();
        assertThat(result.isRequiresOrganizationUnit()).isEqualTo(expectedValue);
    }

    @Test
    @DisplayName("Powinien poprawnie implementować metodę createDto")
    void createDto_shouldCreateProperDtoInstance() {
        // when
        UserGroupDTO result = userGroupMapper.toDto(adminGroup);

        // then
        assertThat(result).isInstanceOf(UserGroupDTO.class);
    }
}
