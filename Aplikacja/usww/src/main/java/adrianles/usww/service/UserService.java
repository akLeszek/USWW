package adrianles.usww.service;

import adrianles.usww.dto.UserDTO;
import adrianles.usww.entity.Ticket;
import adrianles.usww.entity.User;
import adrianles.usww.entity.dictionary.OrganizationUnit;
import adrianles.usww.entity.dictionary.UserGroup;
import adrianles.usww.exception.ResourceNotFoundException;
import adrianles.usww.repository.TicketRepository;
import adrianles.usww.repository.UserRepository;
import adrianles.usww.repository.dictionary.OrganizationUnitRepository;
import adrianles.usww.repository.dictionary.UserGroupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final OrganizationUnitRepository organizationUnitRepository;
    private final UserGroupRepository userGroupRepository;
    private final PasswordEncoder passwordEncoder;
    private final TicketRepository ticketRepository;

    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    public UserDTO getUserById(Integer id) {
        User user = findUserById(id);
        return convertToDTO(user);
    }

    private User findUserById(Integer id) {
        return userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    public UserDTO getUserByLogin(String login) {
        User user = userRepository.findByLogin(login).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return convertToDTO(user);
    }

    public UserDTO createUser(UserDTO userDTO) {
        User user = new User();
        user.setLogin(userDTO.getLogin());
        user.setPassword(passwordEncoder.encode(userDTO.getLogin()));
        user.setForename(userDTO.getForename());
        user.setSurname(userDTO.getSurname());
        user.setLoginBan(userDTO.isLoginBan());
        user.setArchive(userDTO.isArchive());
        user.setFirstLogin(true);
        user.setOrganizationUnit(getOrganizationUnit(userDTO));
        user.setUserGroup(getUserGroup(userDTO));

        User savedUser = userRepository.save(user);
        UserDTO savedUserDTO = convertToDTO(savedUser);
        savedUserDTO.setGeneratedPassword(savedUserDTO.getLogin());
        return savedUserDTO;
    }

    private OrganizationUnit getOrganizationUnit(UserDTO userDTO) {
        return organizationUnitRepository.findById(userDTO.getOrganizationUnitId())
                .orElseThrow(() -> new ResourceNotFoundException("OrganizationUnit not found"));
    }

    private UserGroup getUserGroup(UserDTO userDTO) {
        return userGroupRepository.findById(userDTO.getGroupId())
                .orElseThrow(() -> new ResourceNotFoundException("UserGroup not found"));
    }

    public UserDTO changePassword(Integer userId, String currentPassword, String newPassword) {
        User user = findUserById(userId);

        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new IllegalArgumentException("Current password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setFirstLogin(false);
        user.setLastLogin(LocalDateTime.now());

        User savedUser = userRepository.save(user);
        return convertToDTO(savedUser);
    }

    public UserDTO blockUser(Integer userId) {
        User user = findUserById(userId);
        user.setLoginBan(true);
        User savedUser = userRepository.save(user);
        return convertToDTO(savedUser);
    }

    public UserDTO unblockUser(Integer userId) {
        User user = findUserById(userId);
        user.setLoginBan(false);
        User savedUser = userRepository.save(user);
        return convertToDTO(savedUser);
    }

    public UserDTO archiveUser(Integer userId) {
        User user = findUserById(userId);
        user.setArchive(true);
        user.setLoginBan(true);
        User savedUser = userRepository.save(user);
        archiveUserTickets(ticketRepository.findByStudentId(userId));
        archiveUserTickets(ticketRepository.findByOperatorId(userId));
        return convertToDTO(savedUser);
    }

    private void archiveUserTickets(List<Ticket> tickets) {
        tickets.forEach(ticket -> {
            ticket.setArchive(true);
            ticketRepository.save(ticket);
        });
    }

    public List<UserDTO> getArchivedUsers() {
        return userRepository.findAllByArchiveTrue().stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    public UserDTO restoreUser(Integer userId) {
        User user = findUserById(userId);
        user.setArchive(false);
        User savedUser = userRepository.save(user);
        return convertToDTO(savedUser);
    }

    public UserDTO getUserBasicInfo(Integer id) {
        User user = findUserById(id);

        UserDTO basicInfo = new UserDTO();
        basicInfo.setId(user.getId());
        basicInfo.setForename(user.getForename());
        basicInfo.setSurname(user.getSurname());
        return basicInfo;
    }

    private UserDTO convertToDTO(User user) {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setLogin(user.getLogin());
        userDTO.setForename(user.getForename());
        userDTO.setSurname(user.getSurname());
        userDTO.setArchive(user.isArchive());
        userDTO.setFirstLogin(user.isFirstLogin());
        userDTO.setLoginBan(user.isLoginBan());
        userDTO.setLastLogin(user.getLastLogin() != null ? user.getLastLogin().toString() : null);
        userDTO.setGroupId(user.getUserGroup() != null ? user.getUserGroup().getId() : null);
        userDTO.setOrganizationUnitId(user.getOrganizationUnit() != null ? user.getOrganizationUnit().getId() : null);
        userDTO.setGeneratedPassword(null);
        return userDTO;
    }
}
