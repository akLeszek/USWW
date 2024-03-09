package adrianles.usww.entity.dictionary;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "USER_GROUP")
public class UserGroup extends Dictionary {

    public UserGroup() {
    }

    public UserGroup(Integer id, String idn, String name) {
        super(id, idn, name);
    }

}
