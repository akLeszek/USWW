package adrianles.usww.entity.dictionary;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "ORGANIZATION_UNIT")
public class OrganizationUnit extends Dictionary {
    public OrganizationUnit() {
    }

    public OrganizationUnit(Integer id, String idn, String name) {
        super(id, idn, name);
    }
}
