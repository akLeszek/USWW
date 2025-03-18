package adrianles.usww.api.dto.dictionary;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserGroupDTO extends AbstractDictionaryDTO {
    private boolean requiresOrganizationUnit;

    public UserGroupDTO(Integer id, String idn, String name) {
        super(id, idn, name);
    }
}
