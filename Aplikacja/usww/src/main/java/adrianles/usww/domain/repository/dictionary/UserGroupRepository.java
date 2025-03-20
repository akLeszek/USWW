package adrianles.usww.domain.repository.dictionary;

import adrianles.usww.domain.entity.dictionary.UserGroup;
import adrianles.usww.domain.repository.dictionary.DictionaryRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserGroupRepository extends DictionaryRepository<UserGroup> {
    Optional<UserGroup> findByIdn(String idn);
}
