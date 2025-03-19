package adrianles.usww.domain.repositiory.dictionary;

import adrianles.usww.domain.entity.dictionary.UserGroup;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserGroupRepository extends DictionaryRepository<UserGroup> {
    Optional<UserGroup> findByIdn(String idn);
}
