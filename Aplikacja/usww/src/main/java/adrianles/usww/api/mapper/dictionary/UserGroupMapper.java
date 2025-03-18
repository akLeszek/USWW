package adrianles.usww.api.mapper.dictionary;

import adrianles.usww.api.dto.dictionary.UserGroupDTO;
import adrianles.usww.domain.entity.dictionary.UserGroup;
import adrianles.usww.utils.UserGroupUtils;
import org.springframework.stereotype.Component;

@Component
public class UserGroupMapper extends AbstractDictionaryMapper<UserGroup, UserGroupDTO> {

    @Override
    protected UserGroupDTO createDto(Integer id, String idn, String name) {
        return new UserGroupDTO(id, idn, name);
    }

    @Override
    protected void mapSpecificFields(UserGroup entity, UserGroupDTO dto) {
        dto.setRequiresOrganizationUnit(!UserGroupUtils.isAdmin(entity));
    }
}
