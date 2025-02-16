package adrianles.usww.entity.dictionary;

import adrianles.usww.entity.AbstractEntity;
import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@MappedSuperclass
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public abstract class AbstractDictionary extends AbstractEntity {

    @Column(unique = true, nullable = false, length = 15)
    private String idn;

    private String name;
}
