package adrianles.usww.service.facade;

import adrianles.usww.api.dto.dictionary.UserGroupDTO;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
public interface UserGroupService {
    List<UserGroupDTO> getAllUserGroups();

    UserGroupDTO getUserGroupById(Integer id);

    UserGroupDTO getUserGroupByIdn(String idn);
}
