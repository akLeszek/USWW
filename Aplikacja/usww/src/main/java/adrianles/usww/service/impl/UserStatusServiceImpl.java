package adrianles.usww.service.impl;

import adrianles.usww.api.dto.UserDTO;
import adrianles.usww.api.mapper.UserMapper;
import adrianles.usww.domain.entity.Ticket;
import adrianles.usww.domain.entity.User;
import adrianles.usww.domain.repositiory.TicketRepository;
import adrianles.usww.domain.repositiory.UserRepository;
import adrianles.usww.exception.ResourceNotFoundException;
import adrianles.usww.service.facade.UserStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserStatusServiceImpl implements UserStatusService {

    private final UserRepository userRepository;
    private final TicketRepository ticketRepository;
    private final UserMapper userMapper;

    @Override
    public UserDTO blockUser(Integer userId) {
        User user = findUserById(userId);
        user.setLoginBan(true);
        User savedUser = userRepository.save(user);
        return userMapper.toDto(savedUser);
    }

    @Override
    public UserDTO unblockUser(Integer userId) {
        User user = findUserById(userId);
        user.setLoginBan(false);
        User savedUser = userRepository.save(user);
        return userMapper.toDto(savedUser);
    }

    @Override
    public UserDTO archiveUser(Integer userId) {
        User user = findUserById(userId);
        user.setArchive(true);
        user.setLoginBan(true);
        User savedUser = userRepository.save(user);

        archiveUserTickets(ticketRepository.findByStudentId(userId));
        archiveUserTickets(ticketRepository.findByOperatorId(userId));

        return userMapper.toDto(savedUser);
    }

    @Override
    public List<UserDTO> getArchivedUsers() {
        return userRepository.findAllByArchiveTrue().stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDTO restoreUser(Integer userId) {
        User user = findUserById(userId);
        user.setArchive(false);
        User savedUser = userRepository.save(user);
        return userMapper.toDto(savedUser);
    }

    private void archiveUserTickets(List<Ticket> tickets) {
        tickets.forEach(ticket -> {
            ticket.setArchive(true);
            ticketRepository.save(ticket);
        });
    }

    private User findUserById(Integer id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Użytkownik o id " + id + " nie istnieje"));
    }
}
