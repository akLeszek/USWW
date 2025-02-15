package adrianles.usww.entity.dictionary;

import adrianles.usww.entity.AbstractEntity;
import jakarta.persistence.Entity;

public abstract class Dictionary extends AbstractEntity {
    private String idn;
    private String name;

    public Dictionary() {
    }

    public Dictionary(String idn, String name) {
        this.idn = idn;
        this.name = name;
    }

    public String getIdn() {
        return idn;
    }

    public void setIdn(String idn) {
        this.idn = idn;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
