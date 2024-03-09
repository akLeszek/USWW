package adrianles.usww.entity;

import adrianles.usww.entity.dictionary.OrganizationUnit;
import adrianles.usww.entity.dictionary.UserGroup;
import jakarta.persistence.*;

import java.sql.Timestamp;

@Entity
@Table(name = "USER")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String login;
    private byte[] password;
    private String forename;
    private String surname;
    @Column(name = "login_ban")
    private boolean loginBan;
    @Column(name = "last_login")
    private Timestamp lastLogin;
    @ManyToOne
    @JoinColumn(name = "group_id")
    private UserGroup group;
    @ManyToOne
    @JoinColumn(name = "organization_unit_id")
    private OrganizationUnit organizationUnit;
    private boolean archive;

    public User() {
    }

    public User(Integer id, String login, byte[] password, String forename, String surname, boolean loginBan,
                Timestamp lastLogin, UserGroup group, OrganizationUnit organizationUnit, boolean archive) {
        this.id = id;
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

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

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

    public boolean isLoginBan() {
        return loginBan;
    }

    public void setLoginBan(boolean loginBan) {
        this.loginBan = loginBan;
    }

    public Timestamp getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(Timestamp lastLogin) {
        this.lastLogin = lastLogin;
    }

    public UserGroup getGroup() {
        return group;
    }

    public void setGroup(UserGroup group) {
        this.group = group;
    }

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
