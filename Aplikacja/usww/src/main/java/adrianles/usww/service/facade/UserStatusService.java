package adrianles.usww.service.facade;

import adrianles.usww.api.dto.UserDTO;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
public interface UserStatusService {

    @Transactional
    UserDTO blockUser(Integer userId);

    @Transactional
    UserDTO unblockUser(Integer userId);

    @Transactional
    UserDTO archiveUser(Integer userId);

    List<UserDTO> getArchivedUsers();

    @Transactional
    UserDTO restoreUser(Integer userId);
}
