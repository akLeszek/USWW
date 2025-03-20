package adrianles.usww.service.impl;

import adrianles.usww.api.dto.dictionary.TicketStatusDTO;
import adrianles.usww.api.mapper.dictionary.TicketStatusMapper;
import adrianles.usww.domain.entity.dictionary.TicketStatus;
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
}
