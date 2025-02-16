package adrianles.usww.dto.dictionary;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public abstract class AbstractDictionaryDTO {
    private Integer id;
    private String idn;
    private String name;
}
