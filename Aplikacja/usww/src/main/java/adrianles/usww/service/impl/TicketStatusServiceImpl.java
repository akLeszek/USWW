package adrianles.usww.service.impl;

import adrianles.usww.api.dto.dictionary.TicketStatusDTO;
import adrianles.usww.api.mapper.dictionary.TicketStatusMapper;
import adrianles.usww.domain.entity.Ticket;
import adrianles.usww.domain.entity.dictionary.TicketStatus;
import adrianles.usww.domain.repository.TicketRepository;
import adrianles.usww.domain.repository.dictionary.TicketStatusRepository;
import adrianles.usww.exception.ResourceNotFoundException;
import adrianles.usww.service.facade.TicketStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TicketStatusServiceImpl implements TicketStatusService {

    private final TicketStatusRepository ticketStatusRepository;
    private final TicketStatusMapper ticketStatusMapper;
    private final TicketRepository ticketRepository;

    @Override
    public List<TicketStatusDTO> getAllTicketStatuses() {
        return ticketStatusRepository.findAll().stream()
                .map(ticketStatusMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public TicketStatusDTO getTicketStatusById(Integer id) {
        TicketStatus ticketStatus = ticketStatusRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket status with id " + id + " not found"));
        return ticketStatusMapper.toDto(ticketStatus);
    }

    @Override
    public TicketStatusDTO getTicketStatusByIdn(String idn) {
        TicketStatus ticketStatus = ticketStatusRepository.findByIdn(idn)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket status with idn " + idn + " not found"));
        return ticketStatusMapper.toDto(ticketStatus);
    }

    @Override
    public TicketStatusDTO createTicketStatus(TicketStatusDTO statusDTO) {
        if (ticketStatusRepository.findByIdn(statusDTO.getIdn()).isPresent()) {
            throw new IllegalArgumentException("Ticket status with idn " + statusDTO.getIdn() + " already exists");
        }

        TicketStatus ticketStatus = new TicketStatus();
        ticketStatus.setIdn(statusDTO.getIdn());
        ticketStatus.setName(statusDTO.getName());

        TicketStatus savedStatus = ticketStatusRepository.save(ticketStatus);
        return ticketStatusMapper.toDto(savedStatus);
    }

    @Override
    public TicketStatusDTO updateTicketStatus(Integer id, TicketStatusDTO statusDTO) {
        TicketStatus existingStatus = ticketStatusRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket category with id " + id + " not found"));

        if (!existingStatus.getIdn().equals(statusDTO.getIdn()) &&
                ticketStatusRepository.findByIdn(statusDTO.getIdn()).isPresent()) {
            throw new IllegalArgumentException("Ticket category with idn " + statusDTO.getIdn() + " already exists");
        }

        existingStatus.setIdn(statusDTO.getIdn());
        existingStatus.setName(statusDTO.getName());

        TicketStatus updatedStatus = ticketStatusRepository.save(existingStatus);
        return ticketStatusMapper.toDto(updatedStatus);
    }

    @Override
    public void deleteTicketStatus(Integer id) {
        if (!ticketStatusRepository.existsById(id)) {
            throw new IllegalArgumentException("Ticket status with id " + id + " not found");
        }

        List<Ticket> ticketsWithStatus = ticketRepository.findByStatusId(id);
        if (!ticketsWithStatus.isEmpty()) {
            throw new IllegalArgumentException("Cannot delete status that is used by tickets");
        }

        ticketStatusRepository.deleteById(id);
    }
}
