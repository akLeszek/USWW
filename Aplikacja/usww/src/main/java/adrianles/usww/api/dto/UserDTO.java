package adrianles.usww.api.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private Integer id;
    @NotNull(message = "Login cannot be null")
    private String login;
    private String forename;
    private String surname;
    private boolean loginBan;
    private String lastLogin;
    @NotNull(message = "User group cannot be null")
    private Integer groupId;
    private Integer organizationUnitId;
    private boolean archive;
    private String generatedPassword;
    private boolean firstLogin;
}
