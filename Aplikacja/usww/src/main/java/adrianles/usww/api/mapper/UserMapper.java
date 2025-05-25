package adrianles.usww.api.mapper;

import adrianles.usww.api.dto.UserDTO;
import adrianles.usww.domain.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserDTO toDto(User user) {
        if (user == null) {
            return null;
        }

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

        return userDTO;
    }

    public UserDTO toBasicInfoDto(User user) {
        if (user == null) {
            return null;
        }

        UserDTO basicInfo = new UserDTO();
        basicInfo.setId(user.getId());
        basicInfo.setLogin(user.getLogin());
        basicInfo.setForename(user.getForename());
        basicInfo.setSurname(user.getSurname());

        return basicInfo;
    }
}
