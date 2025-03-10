package adrianles.usww.service.facade;

import adrianles.usww.api.dto.UserDTO;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
public interface UserService {

    List<UserDTO> getAllUsers();

    UserDTO getUserById(Integer id);

    UserDTO getUserByLogin(String login);

    @Transactional
    UserDTO createUser(UserDTO userDTO);

    UserDTO getUserBasicInfo(Integer id);
}
