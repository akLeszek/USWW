package adrianles.usww.unit.domain.specification;

import adrianles.usww.api.dto.TicketFilterCriteriaDTO;
import adrianles.usww.domain.entity.Ticket;
import adrianles.usww.domain.specification.TicketSpecifications;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.jpa.domain.Specification;

import java.time.format.DateTimeParseException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TicketSpecificationsTest {

    @Test
    @DisplayName("buildSpecification should handle null criteria")
    void buildSpecification_shouldHandleNullCriteria() {
        Specification<Ticket> result = TicketSpecifications.buildSpecification(null);
        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("buildSpecification should handle empty criteria")
    void buildSpecification_shouldHandleEmptyCriteria() {
        TicketFilterCriteriaDTO criteria = new TicketFilterCriteriaDTO();
        Specification<Ticket> result = TicketSpecifications.buildSpecification(criteria);
        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("buildSpecification should handle criteria with valid fields")
    void buildSpecification_shouldHandleCriteriaWithValidFields() {
        TicketFilterCriteriaDTO criteria = new TicketFilterCriteriaDTO();
        criteria.setTitle("test");
        criteria.setStudentId(1);
        criteria.setOperatorId(2);
        criteria.setStatusId(3);
        criteria.setCategoryId(4);
        criteria.setPriorityId(5);
        criteria.setArchive(true);

        Specification<Ticket> result = TicketSpecifications.buildSpecification(criteria);
        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("isInsertedAfter should throw DateTimeParseException for invalid date format")
    void isInsertedAfter_shouldThrowException_forInvalidDateFormat() {
        assertThatThrownBy(() -> TicketSpecifications.isInsertedAfter("invalid-date"))
                .isInstanceOf(DateTimeParseException.class);
    }

    @Test
    @DisplayName("isInsertedBefore should throw DateTimeParseException for invalid date format")
    void isInsertedBefore_shouldThrowException_forInvalidDateFormat() {
        assertThatThrownBy(() -> TicketSpecifications.isInsertedBefore("invalid-date"))
                .isInstanceOf(DateTimeParseException.class);
    }

    @Test
    @DisplayName("hasLastActivityAfter should throw DateTimeParseException for invalid date format")
    void hasLastActivityAfter_shouldThrowException_forInvalidDateFormat() {
        assertThatThrownBy(() -> TicketSpecifications.hasLastActivityAfter("invalid-date"))
                .isInstanceOf(DateTimeParseException.class);
    }

    @Test
    @DisplayName("hasLastActivityBefore should throw DateTimeParseException for invalid date format")
    void hasLastActivityBefore_shouldThrowException_forInvalidDateFormat() {
        assertThatThrownBy(() -> TicketSpecifications.hasLastActivityBefore("invalid-date"))
                .isInstanceOf(DateTimeParseException.class);
    }

    @Test
    @DisplayName("buildSpecification should handle criteria with date in valid format")
    void buildSpecification_shouldHandleCriteriaWithValidDateFormat() {
        TicketFilterCriteriaDTO criteria = new TicketFilterCriteriaDTO();
        criteria.setFromDate("2024-02-16T10:00:00");
        criteria.setToDate("2024-02-16T12:00:00");
        criteria.setLastActivityFrom("2024-02-16T10:00:00");
        criteria.setLastActivityTo("2024-02-16T12:00:00");

        Specification<Ticket> result = TicketSpecifications.buildSpecification(criteria);
        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("buildSpecification should throw exception when fromDate has invalid format")
    void buildSpecification_shouldThrowException_whenFromDateHasInvalidFormat() {
        TicketFilterCriteriaDTO criteria = new TicketFilterCriteriaDTO();
        criteria.setFromDate("invalid-date");

        assertThatThrownBy(() -> TicketSpecifications.buildSpecification(criteria))
                .isInstanceOf(DateTimeParseException.class);
    }

    @Test
    @DisplayName("buildSpecification should throw exception when toDate has invalid format")
    void buildSpecification_shouldThrowException_whenToDateHasInvalidFormat() {
        TicketFilterCriteriaDTO criteria = new TicketFilterCriteriaDTO();
        criteria.setToDate("invalid-date");

        assertThatThrownBy(() -> TicketSpecifications.buildSpecification(criteria))
                .isInstanceOf(DateTimeParseException.class);
    }

    @Test
    @DisplayName("buildSpecification should throw exception when lastActivityFrom has invalid format")
    void buildSpecification_shouldThrowException_whenLastActivityFromHasInvalidFormat() {
        TicketFilterCriteriaDTO criteria = new TicketFilterCriteriaDTO();
        criteria.setLastActivityFrom("invalid-date");

        assertThatThrownBy(() -> TicketSpecifications.buildSpecification(criteria))
                .isInstanceOf(DateTimeParseException.class);
    }

    @Test
    @DisplayName("buildSpecification should throw exception when lastActivityTo has invalid format")
    void buildSpecification_shouldThrowException_whenLastActivityToHasInvalidFormat() {
        TicketFilterCriteriaDTO criteria = new TicketFilterCriteriaDTO();
        criteria.setLastActivityTo("invalid-date");

        assertThatThrownBy(() -> TicketSpecifications.buildSpecification(criteria))
                .isInstanceOf(DateTimeParseException.class);
    }
}
