package adrianles.usww.repository;

import adrianles.usww.entity.Ticket;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Integer>, JpaSpecificationExecutor<Ticket> {

    List<Ticket> findByStudentId(Integer id);

    List<Ticket> findByOperatorId(Integer id);

    List<Ticket> findByArchiveTrue();

    List<Ticket> findByArchiveFalse();

    Page<Ticket> findAll(Specification<Ticket> specification, Pageable pageable);

    List<Ticket> findByPriorityId(Integer priorityId);

    List<Ticket> findByCategoryId(Integer categoryId);
}
