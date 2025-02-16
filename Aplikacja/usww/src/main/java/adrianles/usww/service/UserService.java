package adrianles.usww.service;

import adrianles.usww.dto.UserDTO;
import adrianles.usww.entity.User;
import adrianles.usww.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public UserDTO getUserById(Integer id) {
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        return convertToDTO(user);
    }

    public UserDTO createUser(UserDTO userDTO) {
        User user = new User();
        user.setLogin(userDTO.getLogin());
        user.setForename(userDTO.getForename());
        user.setSurname(userDTO.getSurname());
        user.setLoginBan(userDTO.isLoginBan());
        user.setArchive(userDTO.isArchive());

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
                user.isArchive()
        );
    }
}
