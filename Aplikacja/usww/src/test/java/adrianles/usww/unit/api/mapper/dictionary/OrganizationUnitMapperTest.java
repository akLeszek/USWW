package adrianles.usww.unit.api.mapper.dictionary;

import adrianles.usww.api.dto.dictionary.OrganizationUnitDTO;
import adrianles.usww.api.mapper.dictionary.OrganizationUnitMapper;
import adrianles.usww.domain.entity.dictionary.OrganizationUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class OrganizationUnitMapperTest {

    private OrganizationUnitMapper organizationUnitMapper;
    private OrganizationUnit organizationUnit;

    @BeforeEach
    void setUp() {
        organizationUnitMapper = new OrganizationUnitMapper();
        organizationUnit = new OrganizationUnit();
        organizationUnit.setId(1);
        organizationUnit.setIdn("IT");
        organizationUnit.setName("Dział IT");
    }

    @Test
    @DisplayName("Powinien poprawnie mapować encję OrganizationUnit na DTO")
    void toDto_shouldMapEntityToDto() {
        // when
        OrganizationUnitDTO result = organizationUnitMapper.toDto(organizationUnit);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1);
        assertThat(result.getIdn()).isEqualTo("IT");
        assertThat(result.getName()).isEqualTo("Dział IT");
    }

    @Test
    @DisplayName("Powinien zwrócić null gdy encja jest null")
    void toDto_shouldReturnNullWhenEntityIsNull() {
        // when
        OrganizationUnitDTO result = organizationUnitMapper.toDto(null);

        // then
        assertThat(result).isNull();
    }

    @Test
    @DisplayName("Powinien poprawnie mapować encję z polami null")
    void toDto_shouldMapEntityWithNullFields() {
        // given
        OrganizationUnit unitWithNulls = new OrganizationUnit();
        unitWithNulls.setId(2);
        unitWithNulls.setIdn(null);
        unitWithNulls.setName(null);

        // when
        OrganizationUnitDTO result = organizationUnitMapper.toDto(unitWithNulls);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(2);
        assertThat(result.getIdn()).isNull();
        assertThat(result.getName()).isNull();
    }

    @Test
    @DisplayName("Powinien poprawnie implementować metodę createDto")
    void createDto_shouldCreateProperDtoInstance() {
        // when
        OrganizationUnitDTO result = organizationUnitMapper.toDto(organizationUnit);

        // then
        assertThat(result).isInstanceOf(OrganizationUnitDTO.class);
    }
}
