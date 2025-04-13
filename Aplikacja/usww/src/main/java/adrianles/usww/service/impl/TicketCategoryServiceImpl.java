package adrianles.usww.service.impl;

import adrianles.usww.api.dto.dictionary.TicketCategoryDTO;
import adrianles.usww.api.mapper.dictionary.TicketCategoryMapper;
import adrianles.usww.domain.entity.Ticket;
import adrianles.usww.domain.entity.dictionary.TicketCategory;
import adrianles.usww.domain.repository.TicketRepository;
import adrianles.usww.domain.repository.dictionary.TicketCategoryRepository;
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
    private final TicketRepository ticketRepository;
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

    @Override
    public TicketCategoryDTO createTicketCategory(TicketCategoryDTO categoryDTO) {
        if (ticketCategoryRepository.findByIdn(categoryDTO.getIdn()).isPresent()) {
            throw new IllegalArgumentException("Ticket category with idn " + categoryDTO.getIdn() + " already exists");
        }

        TicketCategory ticketCategory = new TicketCategory();
        ticketCategory.setIdn(categoryDTO.getIdn());
        ticketCategory.setName(categoryDTO.getName());

        TicketCategory savedCategory = ticketCategoryRepository.save(ticketCategory);
        return ticketCategoryMapper.toDto(savedCategory);
    }

    @Override
    public TicketCategoryDTO updateTicketCategory(Integer id, TicketCategoryDTO categoryDTO) {
        TicketCategory existingCategory = ticketCategoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket category with id " + id + " not found"));

        if (!existingCategory.getIdn().equals(categoryDTO.getIdn()) &&
                ticketCategoryRepository.findByIdn(categoryDTO.getIdn()).isPresent()) {
            throw new IllegalArgumentException("Ticket category with idn " + categoryDTO.getIdn() + " already exists");
        }

        existingCategory.setIdn(categoryDTO.getIdn());
        existingCategory.setName(categoryDTO.getName());

        TicketCategory updatedCategory = ticketCategoryRepository.save(existingCategory);
        return ticketCategoryMapper.toDto(updatedCategory);
    }

    @Override
    public void deleteTicketCategory(Integer id) {
        if (!ticketCategoryRepository.existsById(id)) {
            throw new ResourceNotFoundException("Ticket category with id " + id + " not found");
        }

        List<Ticket> ticketsWithCategory = ticketRepository.findByCategoryId(id);
        if (!ticketsWithCategory.isEmpty()) {
            throw new IllegalArgumentException("Cannot delete category that is used by tickets");
        }

        ticketCategoryRepository.deleteById(id);
    }
}
