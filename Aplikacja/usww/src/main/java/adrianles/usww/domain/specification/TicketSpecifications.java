package adrianles.usww.domain.specification;

import adrianles.usww.api.dto.TicketFilterCriteriaDTO;
import adrianles.usww.domain.entity.Ticket;
import adrianles.usww.domain.entity.User;
import adrianles.usww.utils.Constants;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;

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
        } else if (Boolean.TRUE.equals(criteria.getUnassigned())) {
            spec = spec.and(isUnassigned());
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

        if (criteria.getStudentOrganizationUnitId() != null) {
            spec = spec.and(hasStudentOrganizationUnitId(criteria.getStudentOrganizationUnitId()));
        }

        if (criteria.getOperatorOrganizationUnitId() != null) {
            spec = spec.and(hasOperatorOrganizationUnitId(criteria.getOperatorOrganizationUnitId()));
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

    public static Specification<Ticket> hasStudentOrganizationUnitId(Integer organizationUnitId) {
        return (root, query, cb) -> cb.equal(root.get("student").get("organizationUnit").get("id"), organizationUnitId);
    }

    public static Specification<Ticket> hasOperatorOrganizationUnitId(Integer organizationUnitId) {
        return (root, query, cb) -> cb.equal(root.get("operator").get("organizationUnit").get("id"), organizationUnitId);
    }

    public static Specification<Ticket> isUnassigned() {
        return (root, query, cb) -> cb.or(
                cb.isNull(root.get("operator")),
                cb.equal(root.get("operator").get("login"), Constants.DEFAULT_OPERATOR_LOGIN)
        );
    }

    public static Specification<Ticket> accessibleByUser(Integer userId, Collection<String> userRoles) {
        Specification<Ticket> spec = Specification.where(null);

        if (userRoles.contains("ADMIN")) {
            return spec;
        }

        if (userRoles.contains("OPERATOR")) {
            return spec.or(hasOperatorId(userId))
                    .or(hasSameOrganizationUnitAs(userId))
                    .or(isUnassignedOrDefaultOperator());
        }

        return spec.and(hasStudentId(userId));
    }

    public static Specification<Ticket> hasSameOrganizationUnitAs(Integer userId) {
        return (root, query, cb) -> {
            Subquery<Integer> orgUnitSubquery = query.subquery(Integer.class);
            Root<User> userRoot = orgUnitSubquery.from(User.class);

            orgUnitSubquery.select(userRoot.get("organizationUnit").get("id"))
                    .where(cb.equal(userRoot.get("id"), userId));

            Join<Ticket, User> studentJoin = root.join("student");

            return cb.and(
                    cb.isNotNull(studentJoin.get("organizationUnit")),
                    cb.in(studentJoin.get("organizationUnit").get("id")).value(orgUnitSubquery)
            );
        };
    }

    public static Specification<Ticket> isUnassignedOrDefaultOperator() {
        return (root, query, cb) -> cb.or(
                cb.isNull(root.get("operator")),
                cb.equal(root.get("operator").get("login"), Constants.DEFAULT_OPERATOR_LOGIN)
        );
    }
}
