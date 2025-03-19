package adrianles.usww.service.facade;


import adrianles.usww.api.dto.UserDTO;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public interface UserPasswordService {

    @Transactional
    UserDTO changePassword(Integer userId, String currentPassword, String newPassword);

    @Transactional
    UserDTO resetPassword(Integer userId);
}
