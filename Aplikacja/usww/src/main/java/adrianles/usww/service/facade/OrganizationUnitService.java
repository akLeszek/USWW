package adrianles.usww.service.facade;

import adrianles.usww.api.dto.dictionary.OrganizationUnitDTO;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
public interface OrganizationUnitService {

    List<OrganizationUnitDTO> getAllOrganizationUnits();

    OrganizationUnitDTO getOrganizationUnitById(Integer id);

    OrganizationUnitDTO getOrganizationUnitByIdn(String idn);

}
