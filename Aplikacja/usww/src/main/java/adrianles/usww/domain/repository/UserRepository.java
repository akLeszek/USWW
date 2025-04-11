package adrianles.usww.domain.repository;

import adrianles.usww.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByLogin(String login);

    List<User> findAllByArchiveTrue();

    List<User> findByOrganizationUnitId(Integer organizationUnitId);
}
