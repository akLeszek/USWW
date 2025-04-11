package adrianles.usww.domain.repository;

import adrianles.usww.domain.entity.Ticket;
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

    int countByStudentId(Integer studentId);

    int countByOperatorId(Integer operatorId);

    int countByStudentIdAndStatusId(Integer studentId, Integer statusId);

    int countByOperatorIdAndStatusId(Integer operatorId, Integer statusId);

    int countByStatusId(Integer statusId);

    int countByStudentIdAndArchiveFalseAndStatusIdNot(Integer studentId, Integer statusId);

    int countByOperatorIdAndArchiveFalseAndStatusIdNot(Integer operatorId, Integer statusId);

    int countByArchiveFalseAndStatusIdNot(Integer statusId);

    Page<Ticket> findByStudentId(Integer studentId, Pageable pageable);

    Page<Ticket> findByOperatorId(Integer operatorId, Pageable pageable);

    List<Ticket> findByStudentOrganizationUnitId(Integer organizationUnitId);

    List<Ticket> findByOperatorOrganizationUnitId(Integer organizationUnitId);

    int countByStudentOrganizationUnitId(Integer organizationUnitId);

    int countByOperatorOrganizationUnitId(Integer organizationUnitId);

    Page<Ticket> findByStudentOrganizationUnitId(Integer organizationUnitId, Pageable pageable);

    Page<Ticket> findByOperatorOrganizationUnitId(Integer organizationUnitId, Pageable pageable);

    List<Ticket> findByOperatorIsNullOrOperatorLogin(String login);

    int countByOperatorIsNullOrOperatorLogin(String login);

    Page<Ticket> findByOperatorIsNullOrOperatorLogin(String login, Pageable pageable);
}
