package adrianles.usww.service.repository;

import adrianles.usww.entity.AbstractEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommonRepository<E extends AbstractEntity> extends JpaRepository<E, Integer> {
}
