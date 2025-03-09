package adrianles.usww.domain.specification;

import adrianles.usww.api.dto.TicketFilterCriteriaDTO;
import adrianles.usww.domain.entity.Ticket;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TicketSpecifications {

    public static Specification<Ticket> buildSpecification(TicketFilterCriteriaDTO criteria) {
        Specification<Ticket> spec = Specification.where(null);

        if (criteria == null) {
            return spec;
        }

        if (StringUtils.hasText(criteria.getTitle())) {
            spec = spec.and(hasTitle(criteria.getTitle()));
        }

        if (criteria.getStudentId() != null) {
            spec = spec.and(hasStudentId(criteria.getStudentId()));
        }

        if (criteria.getOperatorId() != null) {
            spec = spec.and(hasOperatorId(criteria.getOperatorId()));
        }

        if (criteria.getStatusId() != null) {
            spec = spec.and(hasStatusId(criteria.getStatusId()));
        }

        if (criteria.getCategoryId() != null) {
            spec = spec.and(hasCategoryId(criteria.getCategoryId()));
        }

        if (criteria.getPriorityId() != null) {
            spec = spec.and(hasPriorityId(criteria.getPriorityId()));
        }

        if (criteria.getArchive() != null) {
            spec = spec.and(hasArchiveStatus(criteria.getArchive()));
        }

        if (StringUtils.hasText(criteria.getFromDate())) {
            spec = spec.and(isInsertedAfter(criteria.getFromDate()));
        }

        if (StringUtils.hasText(criteria.getToDate())) {
            spec = spec.and(isInsertedBefore(criteria.getToDate()));
        }

        if (StringUtils.hasText(criteria.getLastActivityFrom())) {
            spec = spec.and(hasLastActivityAfter(criteria.getLastActivityFrom()));
        }

        if (StringUtils.hasText(criteria.getLastActivityTo())) {
            spec = spec.and(hasLastActivityBefore(criteria.getLastActivityTo()));
        }

        return spec;
    }

    public static Specification<Ticket> hasTitle(String title) {
        return (Root<Ticket> root, CriteriaQuery<?> query, CriteriaBuilder cb) ->
                cb.like(cb.lower(root.get("title")), "%" + title.toLowerCase() + "%");
    }

    public static Specification<Ticket> hasStudentId(Integer studentId) {
        return (root, query, cb) -> cb.equal(root.get("student").get("id"), studentId);
    }

    public static Specification<Ticket> hasOperatorId(Integer operatorId) {
        return (root, query, cb) -> cb.equal(root.get("operator").get("id"), operatorId);
    }

    public static Specification<Ticket> hasStatusId(Integer statusId) {
        return (root, query, cb) -> cb.equal(root.get("status").get("id"), statusId);
    }

    public static Specification<Ticket> hasCategoryId(Integer categoryId) {
        return (root, query, cb) -> cb.equal(root.get("category").get("id"), categoryId);
    }

    public static Specification<Ticket> hasPriorityId(Integer priorityId) {
        return (root, query, cb) -> cb.equal(root.get("priority").get("id"), priorityId);
    }

    public static Specification<Ticket> hasArchiveStatus(Boolean archive) {
        return (root, query, cb) -> cb.equal(root.get("archive"), archive);
    }

    public static Specification<Ticket> isInsertedAfter(String fromDate) {
        LocalDateTime date = LocalDateTime.parse(fromDate, DateTimeFormatter.ISO_DATE_TIME);
        return (root, query, cb) -> cb.greaterThanOrEqualTo(root.get("insertedDate"), date);
    }

    public static Specification<Ticket> isInsertedBefore(String toDate) {
        LocalDateTime date = LocalDateTime.parse(toDate, DateTimeFormatter.ISO_DATE_TIME);
        return (root, query, cb) -> cb.lessThanOrEqualTo(root.get("insertedDate"), date);
    }

    public static Specification<Ticket> hasLastActivityAfter(String fromDate) {
        LocalDateTime date = LocalDateTime.parse(fromDate, DateTimeFormatter.ISO_DATE_TIME);
        return (root, query, cb) -> cb.greaterThanOrEqualTo(root.get("lastActivityDate"), date);
    }

    public static Specification<Ticket> hasLastActivityBefore(String toDate) {
        LocalDateTime date = LocalDateTime.parse(toDate, DateTimeFormatter.ISO_DATE_TIME);
        return (root, query, cb) -> cb.lessThanOrEqualTo(root.get("lastActivityDate"), date);
    }
}
