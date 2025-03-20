package adrianles.usww.unit.domain.dictionary;

import adrianles.usww.domain.entity.dictionary.*;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Set;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class DictionaryEntitiesTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    private static Stream<Arguments> provideDictionaryEntities() {
        return Stream.of(
                Arguments.of(new UserGroup(), "UserGroup"),
                Arguments.of(new OrganizationUnit(), "OrganizationUnit"),
                Arguments.of(new TicketCategory(), "TicketCategory"),
                Arguments.of(new TicketStatus(), "TicketStatus"),
                Arguments.of(new TicketPriority(), "TicketPriority")
        );
    }

    @ParameterizedTest(name = "Powinien poprawnie stworzyć encję {1}")
    @MethodSource("provideDictionaryEntities")
    void shouldCreateDictionaryEntity(AbstractDictionary entity, String entityName) {
        entity.setId(1);
        entity.setIdn("TEST_IDN");
        entity.setName("Test Name");

        assertThat(entity).isNotNull();
        assertThat(entity.getId()).isEqualTo(1);
        assertThat(entity.getIdn()).isEqualTo("TEST_IDN");
        assertThat(entity.getName()).isEqualTo("Test Name");
    }

    @ParameterizedTest(name = "Powinien zwrócić błąd walidacji dla braku wymaganego pola idn w encji {1}")
    @MethodSource("provideDictionaryEntities")
    void shouldFailValidationWhenIdnIsNull(AbstractDictionary entity, String entityName) {
        entity.setId(1);
        entity.setIdn(null);
        entity.setName("Test Name");

        Set<ConstraintViolation<AbstractDictionary>> violations = validator.validate(entity);

        assertThat(violations).isNotEmpty();
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
                .isEqualTo("Dictionary idn cannot be null");
    }

    @ParameterizedTest(name = "Powinien poprawnie przejść walidację dla wszystkich wymaganych pól w encji {1}")
    @MethodSource("provideDictionaryEntities")
    void shouldPassValidationWithAllRequiredFields(AbstractDictionary entity, String entityName) {
        entity.setId(1);
        entity.setIdn("TEST_IDN");
        entity.setName("Test Name");

        Set<ConstraintViolation<AbstractDictionary>> violations = validator.validate(entity);

        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("Powinien poprawnie porównać dwie identyczne encje UserGroup")
    void shouldCompareEqualUserGroupObjects() {
        UserGroup group1 = new UserGroup();
        group1.setId(1);
        group1.setIdn("ADMIN");
        group1.setName("Administratorzy");

        UserGroup group2 = new UserGroup();
        group2.setId(1);
        group2.setIdn("ADMIN");
        group2.setName("Administratorzy");

        assertThat(group1).isEqualTo(group1);
        assertThat(group1.hashCode()).isEqualTo(group1.hashCode());

        assertThat(group1).isEqualTo(group2);
        assertThat(group1.hashCode()).isEqualTo(group2.hashCode());
    }

    @Test
    @DisplayName("Powinien poprawnie porównać dwie identyczne encje TicketStatus")
    void shouldCompareEqualTicketStatusObjects() {
        TicketStatus status1 = new TicketStatus();
        status1.setId(1);
        status1.setIdn("NOWE");
        status1.setName("Nowe");

        TicketStatus status2 = new TicketStatus();
        status2.setId(1);
        status2.setIdn("NOWE");
        status2.setName("Nowe");

        assertThat(status1).isEqualTo(status1);
        assertThat(status1.hashCode()).isEqualTo(status1.hashCode());

        assertThat(status1).isEqualTo(status2);
        assertThat(status1.hashCode()).isEqualTo(status2.hashCode());
    }

    @Test
    @DisplayName("Powinien poprawnie rozróżnić dwie różne encje TicketCategory")
    void shouldDifferentiateNonEqualTicketCategoryObjects() {
        TicketCategory category1 = new TicketCategory();
        category1.setId(1);
        category1.setIdn("LOGOWANIE");
        category1.setName("Problemy z logowaniem");

        TicketCategory category2 = new TicketCategory();
        category2.setId(2);
        category2.setIdn("LOGOWANIE");
        category2.setName("Problemy z logowaniem");

        assertThat(category1).isNotEqualTo(null);
        assertThat(category1).isNotEqualTo(new Object());

        assertThat(category1).isNotEqualTo(category2);
        assertThat(category1.hashCode()).isNotEqualTo(category2.hashCode());
    }

    @Test
    @DisplayName("Powinien poprawnie rozróżnić dwie różne encje TicketPriority")
    void shouldDifferentiateNonEqualTicketPriorityObjects() {
        TicketPriority priority1 = new TicketPriority();
        priority1.setId(1);
        priority1.setIdn("HIGH");
        priority1.setName("Wysoki");

        TicketPriority priority2 = new TicketPriority();
        priority2.setId(2);
        priority2.setIdn("MEDIUM");
        priority2.setName("Średni");

        assertThat(priority1).isNotEqualTo(priority2);
        assertThat(priority1.hashCode()).isNotEqualTo(priority2.hashCode());
    }
}
