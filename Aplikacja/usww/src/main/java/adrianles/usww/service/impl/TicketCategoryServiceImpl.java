package adrianles.usww.service.impl;

import adrianles.usww.api.dto.dictionary.TicketCategoryDTO;
import adrianles.usww.api.mapper.dictionary.TicketCategoryMapper;
import adrianles.usww.domain.entity.dictionary.TicketCategory;
import adrianles.usww.domain.repositiory.dictionary.TicketCategoryRepository;
import adrianles.usww.exception.ResourceNotFoundException;
import adrianles.usww.service.facade.TicketCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TicketCategoryServiceImpl implements TicketCategoryService {

    private final TicketCategoryRepository ticketCategoryRepository;
    private final TicketCategoryMapper ticketCategoryMapper;

    @Override
    public List<TicketCategoryDTO> getAllTicketCategories() {
        return ticketCategoryRepository.findAll().stream()
                .map(ticketCategoryMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public TicketCategoryDTO getTicketCategoryById(Integer id) {
        TicketCategory ticketCategory = ticketCategoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket category with id " + id + " not found"));
        return ticketCategoryMapper.toDto(ticketCategory);
    }

    @Override
    public TicketCategoryDTO getTicketCategoryByIdn(String idn) {
        TicketCategory ticketCategory = ticketCategoryRepository.findByIdn(idn)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket category with idn " + idn + " not found"));
        return ticketCategoryMapper.toDto(ticketCategory);
    }
}
