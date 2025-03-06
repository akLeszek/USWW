package adrianles.usww.service;

import adrianles.usww.dto.UserDTO;
import adrianles.usww.entity.User;
import adrianles.usww.exception.ResourceNotFoundException;
import adrianles.usww.repository.UserRepository;
import adrianles.usww.repository.dictionary.OrganizationUnitRepository;
import adrianles.usww.repository.dictionary.UserGroupRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
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

    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public UserDTO getUserById(Integer id) {
        User user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return convertToDTO(user);
    }

    public UserDTO createUser(UserDTO userDTO) {
        User user = new User();
        user.setLogin(userDTO.getLogin());
        user.setForename(userDTO.getForename());
        user.setSurname(userDTO.getSurname());
        user.setLoginBan(userDTO.isLoginBan());
        user.setArchive(userDTO.isArchive());
        user.setFirstLogin(true);

        user.setOrganizationUnit(organizationUnitRepository.findById(userDTO.getOrganizationUnitId())
                .orElseThrow(() -> new ResourceNotFoundException("OrganizationUnit not found")));

        user.setUserGroup(userGroupRepository.findById(userDTO.getGroupId())
                .orElseThrow(() -> new ResourceNotFoundException("UserGroup not found")));

        String rawPassword = generateRandomPassword();
        user.setPassword(passwordEncoder.encode(rawPassword));

        User savedUser = userRepository.save(user);

        UserDTO savedUserDTO = convertToDTO(savedUser);
        savedUserDTO.setGeneratedPassword(rawPassword);
        return savedUserDTO;
    }

    private String generateRandomPassword() {
        return RandomStringUtils.random(12, true, true);
    }

    public UserDTO changePassword(Integer userId, String currentPassword, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new IllegalArgumentException("Current password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setFirstLogin(false);
        user.setLastLogin(LocalDateTime.now());

        User savedUser = userRepository.save(user);
        return convertToDTO(savedUser);
    }

    private UserDTO convertToDTO(User user) {
        return new UserDTO(
                user.getId(),
                user.getLogin(),
                user.getForename(),
                user.getSurname(),
                user.isLoginBan(),
                user.getLastLogin() != null ? user.getLastLogin().toString() : null,
                user.getUserGroup() != null ? user.getUserGroup().getId() : null,
                user.getOrganizationUnit() != null ? user.getOrganizationUnit().getId() : null,
                user.isArchive(),
                null,
                user.isFirstLogin()
        );
    }
}
