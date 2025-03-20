package adrianles.usww.domain.repository.dictionary;

import adrianles.usww.domain.entity.dictionary.AbstractDictionary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DictionaryRepository<T extends AbstractDictionary> extends JpaRepository<T, Integer> {

}
