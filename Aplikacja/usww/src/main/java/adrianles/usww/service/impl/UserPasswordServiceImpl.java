package adrianles.usww.service.impl;

import adrianles.usww.api.dto.UserDTO;
import adrianles.usww.api.mapper.UserMapper;
import adrianles.usww.domain.entity.User;
import adrianles.usww.domain.repositiory.UserRepository;
import adrianles.usww.exception.ResourceNotFoundException;
import adrianles.usww.service.facade.UserPasswordService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserPasswordServiceImpl implements UserPasswordService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    @Override
    public UserDTO changePassword(Integer userId, String currentPassword, String newPassword) {
        User user = findUserById(userId);

        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new IllegalArgumentException("Aktualne hasło jest nieprawidłowe");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setFirstLogin(false);
        user.setLastLogin(LocalDateTime.now());

        User savedUser = userRepository.save(user);
        return userMapper.toDto(savedUser);
    }

    @Override
    public UserDTO resetPassword(Integer userId) {
        User user = findUserById(userId);
        String defaultPassword = user.getLogin();
        user.setPassword(passwordEncoder.encode(defaultPassword));
        user.setFirstLogin(true);

        User savedUser = userRepository.save(user);
        UserDTO userDTO = userMapper.toDto(savedUser);
        userDTO.setGeneratedPassword(defaultPassword);
        return userDTO;
    }

    private User findUserById(Integer id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Użytkownik o id " + id + " nie istnieje"));
    }
}
