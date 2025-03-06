package adrianles.usww.entity;

import adrianles.usww.entity.dictionary.OrganizationUnit;
import adrianles.usww.entity.dictionary.UserGroup;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "USERS")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User extends AbstractEntity {

    @NotNull(message = "Login cannot be null")
    @Column(nullable = false, unique = true, length = 32)
    private String login;

    @NotNull(message = "Password cannot be null")
    @Column(nullable = false)
    private String password;

    @Column(length = 32)
    private String forename;

    @Column(length = 64)
    private String surname;

    @Column(nullable = false)
    private boolean loginBan = false;

    private LocalDateTime lastLogin;

    @ManyToOne
    @JoinColumn(name = "group_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_user_user_group"))
    private UserGroup userGroup;

    @ManyToOne
    @JoinColumn(name = "organization_unit_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_user_organization_unit"))
    private OrganizationUnit organizationUnit;

    @Column(nullable = false)
    private boolean archive = false;

    @Column(name = "first_login", nullable = false)
    private boolean firstLogin = true;

}
