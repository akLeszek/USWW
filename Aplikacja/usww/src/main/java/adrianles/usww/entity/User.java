package adrianles.usww.entity;

import adrianles.usww.entity.dictionary.OrganizationUnit;
import adrianles.usww.entity.dictionary.UserGroup;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.sql.Timestamp;

@Entity
@Table(name = "USER")
public class User extends AbstractEntity {
    private String login;
    private byte[] password;
    private String forename;
    private String surname;
    private boolean loginBan;
    private Timestamp lastLogin;
    private UserGroup group;
    private OrganizationUnit organizationUnit;
    private boolean archive;

    public User() {
    }

    public User(String login, byte[] password, String forename, String surname, boolean loginBan,
                Timestamp lastLogin, UserGroup group, OrganizationUnit organizationUnit, boolean archive) {
        this.login = login;
        this.password = password;
        this.forename = forename;
        this.surname = surname;
        this.loginBan = loginBan;
        this.lastLogin = lastLogin;
        this.group = group;
        this.organizationUnit = organizationUnit;
        this.archive = archive;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    @JsonIgnore
    public byte[] getPassword() {
        return password;
    }

    public void setPassword(byte[] password) {
        this.password = password;
    }

    public String getForename() {
        return forename;
    }

    public void setForename(String forename) {
        this.forename = forename;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    @Column(name = "login_ban")
    public boolean isLoginBan() {
        return loginBan;
    }

    public void setLoginBan(boolean loginBan) {
        this.loginBan = loginBan;
    }

    @Column(name = "last_login")
    public Timestamp getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(Timestamp lastLogin) {
        this.lastLogin = lastLogin;
    }

    @ManyToOne
    @JoinColumn(name = "group_id")
    public UserGroup getGroup() {
        return group;
    }

    public void setGroup(UserGroup group) {
        this.group = group;
    }

    @ManyToOne
    @JoinColumn(name = "organization_unit_id")
    public OrganizationUnit getOrganizationUnit() {
        return organizationUnit;
    }

    public void setOrganizationUnit(OrganizationUnit organizationUnit) {
        this.organizationUnit = organizationUnit;
    }

    public boolean isArchive() {
        return archive;
    }

    public void setArchive(boolean archive) {
        this.archive = archive;
    }
}
