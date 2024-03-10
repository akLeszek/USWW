package adrianles.usww;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EntityRepository<Entity, Integer> extends JpaRepository<Entity, Integer> {
}
