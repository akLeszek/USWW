package adrianles.usww.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private Integer id;
    private String login;
    private String forename;
    private String surname;
    private boolean loginBan;
    private String lastLogin;
    private Integer groupId;
    private Integer organizationUnitId;
    private boolean archive;
}
