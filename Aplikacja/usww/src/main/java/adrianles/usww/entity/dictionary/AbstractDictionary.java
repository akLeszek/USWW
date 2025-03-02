package adrianles.usww.entity.dictionary;

import adrianles.usww.entity.AbstractEntity;
import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.validation.constraints.NotNull;
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

    @NotNull(message = "Dictionary idn cannot be null")
    @Column(unique = true, nullable = false, length = 15)
    private String idn;

    private String name;
}
