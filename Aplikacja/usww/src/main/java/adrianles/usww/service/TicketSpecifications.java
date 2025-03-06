package adrianles.usww.service;

import adrianles.usww.dto.TicketFilterCriteriaDTO;
import adrianles.usww.entity.Ticket;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class TicketSpecifications {

    public static Specification<Ticket> buildSpecification(TicketFilterCriteriaDTO criteria) {
        return (Root<Ticket> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Filtrowanie po tytule
            if (StringUtils.hasText(criteria.getTitle())) {
                predicates.add(cb.like(cb.lower(root.get("title")),
                        "%" + criteria.getTitle().toLowerCase() + "%"));
            }

            // Filtrowanie po ID studenta
            if (criteria.getStudentId() != null) {
                predicates.add(cb.equal(root.get("student").get("id"), criteria.getStudentId()));
            }

            // Filtrowanie po ID operatora
            if (criteria.getOperatorId() != null) {
                predicates.add(cb.equal(root.get("operator").get("id"), criteria.getOperatorId()));
            }

            // Filtrowanie po statusie
            if (criteria.getStatusId() != null) {
                predicates.add(cb.equal(root.get("status").get("id"), criteria.getStatusId()));
            }

            // Filtrowanie po kategorii
            if (criteria.getCategoryId() != null) {
                predicates.add(cb.equal(root.get("category").get("id"), criteria.getCategoryId()));
            }

            // Filtrowanie po priorytecie
            if (criteria.getPriorityId() != null) {
                predicates.add(cb.equal(root.get("priority").get("id"), criteria.getPriorityId()));
            }

            // Filtrowanie po archiwizacji
            if (criteria.getArchive() != null) {
                predicates.add(cb.equal(root.get("archive"), criteria.getArchive()));
            }

            // Filtrowanie po dacie utworzenia (zakres)
            if (StringUtils.hasText(criteria.getFromDate())) {
                LocalDateTime fromDate = LocalDateTime.parse(criteria.getFromDate(),
                        DateTimeFormatter.ISO_DATE_TIME);
                predicates.add(cb.greaterThanOrEqualTo(root.get("insertedDate"), fromDate));
            }

            if (StringUtils.hasText(criteria.getToDate())) {
                LocalDateTime toDate = LocalDateTime.parse(criteria.getToDate(),
                        DateTimeFormatter.ISO_DATE_TIME);
                predicates.add(cb.lessThanOrEqualTo(root.get("insertedDate"), toDate));
            }

            // Filtrowanie po dacie ostatniej aktywności
            if (StringUtils.hasText(criteria.getLastActivityFrom())) {
                LocalDateTime lastActivityFrom = LocalDateTime.parse(criteria.getLastActivityFrom(),
                        DateTimeFormatter.ISO_DATE_TIME);
                predicates.add(cb.greaterThanOrEqualTo(root.get("lastActivityDate"), lastActivityFrom));
            }

            if (StringUtils.hasText(criteria.getLastActivityTo())) {
                LocalDateTime lastActivityTo = LocalDateTime.parse(criteria.getLastActivityTo(),
                        DateTimeFormatter.ISO_DATE_TIME);
                predicates.add(cb.lessThanOrEqualTo(root.get("lastActivityDate"), lastActivityTo));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
