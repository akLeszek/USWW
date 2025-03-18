package adrianles.usww.api.mapper.dictionary;

import adrianles.usww.api.dto.dictionary.OrganizationUnitDTO;
import adrianles.usww.domain.entity.dictionary.OrganizationUnit;
import org.springframework.stereotype.Component;

@Component
public class OrganizationUnitMapper extends AbstractDictionaryMapper<OrganizationUnit, OrganizationUnitDTO> {

    @Override
    protected OrganizationUnitDTO createDto(Integer id, String idn, String name) {
        return new OrganizationUnitDTO(id, idn, name);
    }
}
