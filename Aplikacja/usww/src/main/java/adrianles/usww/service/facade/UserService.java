package adrianles.usww.service.facade;

import adrianles.usww.api.dto.UserDTO;
import adrianles.usww.domain.entity.User;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
public interface UserService {

    List<UserDTO> getAllUsers();

    List<UserDTO> getAllOperators();

    UserDTO getUserById(Integer id);

    UserDTO getUserByLogin(String login);

    @Transactional
    UserDTO createUser(UserDTO userDTO);

    @Transactional
    UserDTO updateUser(Integer userId, UserDTO userDTO);

    UserDTO getUserBasicInfo(Integer id);

    List<UserDTO> getOperatorsByOrganizationUnitId(Integer organizationUnitId);

    List<UserDTO> getOperatorsBySameOrganizationUnitAsStudent(Integer studentId);

    User findUserById(Integer userId);
}
