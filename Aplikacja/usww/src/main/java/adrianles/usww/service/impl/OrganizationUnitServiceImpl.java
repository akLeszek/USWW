package adrianles.usww.service.impl;

import adrianles.usww.api.dto.dictionary.OrganizationUnitDTO;
import adrianles.usww.api.mapper.dictionary.OrganizationUnitMapper;
import adrianles.usww.domain.entity.dictionary.OrganizationUnit;
import adrianles.usww.domain.repository.dictionary.OrganizationUnitRepository;
import adrianles.usww.exception.ResourceNotFoundException;
import adrianles.usww.service.facade.OrganizationUnitService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class OrganizationUnitServiceImpl implements OrganizationUnitService {
    private final OrganizationUnitRepository organizationUnitRepository;
    private final OrganizationUnitMapper organizationUnitMapper;

    @Override
    public List<OrganizationUnitDTO> getAllOrganizationUnits() {
        return organizationUnitRepository.findAll().stream()
                .map(organizationUnitMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public OrganizationUnitDTO getOrganizationUnitById(Integer id) {
        OrganizationUnit organizationUnit = organizationUnitRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Organization unit with id " + id + " not found"));
        return organizationUnitMapper.toDto(organizationUnit);
    }

    @Override
    public OrganizationUnitDTO getOrganizationUnitByIdn(String idn) {
        OrganizationUnit organizationUnit = organizationUnitRepository.findByIdn(idn)
                .orElseThrow(() -> new ResourceNotFoundException("Organization unit with idn " + idn + " not found"));
        return organizationUnitMapper.toDto(organizationUnit);
    }
}
