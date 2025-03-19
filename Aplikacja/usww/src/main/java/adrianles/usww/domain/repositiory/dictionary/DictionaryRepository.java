package adrianles.usww.domain.repositiory.dictionary;

import adrianles.usww.domain.entity.dictionary.AbstractDictionary;
import jakarta.persistence.MappedSuperclass;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DictionaryRepository<T extends AbstractDictionary> extends JpaRepository<T, Integer> {

}
