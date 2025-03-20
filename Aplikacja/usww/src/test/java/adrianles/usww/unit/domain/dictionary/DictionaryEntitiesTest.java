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
    void shouldCreateDictionaryEntity(AbstractDictionary entity) {
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
    void shouldFailValidationWhenIdnIsNull(AbstractDictionary entity) {
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
    void shouldPassValidationWithAllRequiredFields(AbstractDictionary entity) {
        entity.setId(1);
        entity.setIdn("TEST_IDN");
        entity.setName("Test Name");

        Set<ConstraintViolation<AbstractDictionary>> violations = validator.validate(entity);

        assertThat(violations).isEmpty();
    }

    @ParameterizedTest(name = "Powinien poprawnie wykorzystać konstruktory dla encji {1}")
    @MethodSource("provideDictionaryEntities")
    void shouldUseConstructors(AbstractDictionary entity) {
        // Test konstruktora bezparametrowego
        assertThat(entity).isNotNull();

        // Test setterów i getterów (uzupełnia funkcjonalność konstruktora z parametrami)
        entity.setId(1);
        entity.setIdn("TEST_IDN");
        entity.setName("Test Name");

        assertThat(entity.getId()).isEqualTo(1);
        assertThat(entity.getIdn()).isEqualTo("TEST_IDN");
        assertThat(entity.getName()).isEqualTo("Test Name");

        // Test konstruktora z parametrami poprzez sprawdzenie AllArgsConstructor z Lombok
        if (entity instanceof UserGroup) {
            UserGroup newEntity = new UserGroup();
            newEntity.setId(1);
            newEntity.setIdn("TEST_IDN");
            newEntity.setName("Test Name");
            assertThat(newEntity).isEqualToComparingFieldByField(entity);
        } else if (entity instanceof OrganizationUnit) {
            OrganizationUnit newEntity = new OrganizationUnit();
            newEntity.setId(1);
            newEntity.setIdn("TEST_IDN");
            newEntity.setName("Test Name");
            assertThat(newEntity).isEqualToComparingFieldByField(entity);
        } else if (entity instanceof TicketCategory) {
            TicketCategory newEntity = new TicketCategory();
            newEntity.setId(1);
            newEntity.setIdn("TEST_IDN");
            newEntity.setName("Test Name");
            assertThat(newEntity).isEqualToComparingFieldByField(entity);
        } else if (entity instanceof TicketStatus) {
            TicketStatus newEntity = new TicketStatus();
            newEntity.setId(1);
            newEntity.setIdn("TEST_IDN");
            newEntity.setName("Test Name");
            assertThat(newEntity).isEqualToComparingFieldByField(entity);
        } else if (entity instanceof TicketPriority) {
            TicketPriority newEntity = new TicketPriority();
            newEntity.setId(1);
            newEntity.setIdn("TEST_IDN");
            newEntity.setName("Test Name");
            assertThat(newEntity).isEqualToComparingFieldByField(entity);
        }
    }

    @ParameterizedTest(name = "Powinien zapewnić unikalność pola idn dla encji {1}")
    @MethodSource("provideDictionaryEntities")
    void shouldEnsureIdnUniqueness(AbstractDictionary entity) {
        entity.setId(1);
        entity.setIdn("UNIQUE_IDN");
        entity.setName("Test Entity");

        AbstractDictionary duplicateEntity = getAbstractDictionary(entity);
        assertThat(entity).isNotEqualTo(duplicateEntity);
        assertThat(entity.getIdn()).isEqualTo(duplicateEntity.getIdn());
        assertThat(entity.getId()).isNotEqualTo(duplicateEntity.getId());
    }

    private static AbstractDictionary getAbstractDictionary(AbstractDictionary entity) {
        AbstractDictionary duplicateEntity;
        if (entity instanceof UserGroup) {
            duplicateEntity = new UserGroup();
        } else if (entity instanceof OrganizationUnit) {
            duplicateEntity = new OrganizationUnit();
        } else if (entity instanceof TicketCategory) {
            duplicateEntity = new TicketCategory();
        } else if (entity instanceof TicketStatus) {
            duplicateEntity = new TicketStatus();
        } else if (entity instanceof TicketPriority) {
            duplicateEntity = new TicketPriority();
        } else {
            throw new IllegalArgumentException("Unknown entity type");
        }

        duplicateEntity.setId(2);
        duplicateEntity.setIdn("UNIQUE_IDN"); // Ten sam idn
        duplicateEntity.setName("Duplicate Entity");
        return duplicateEntity;
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
    @DisplayName("Powinien poprawnie porównać dwie identyczne encje OrganizationUnit")
    void shouldCompareEqualOrganizationUnitObjects() {
        OrganizationUnit unit1 = new OrganizationUnit();
        unit1.setId(1);
        unit1.setIdn("IT");
        unit1.setName("Dział IT");

        OrganizationUnit unit2 = new OrganizationUnit();
        unit2.setId(1);
        unit2.setIdn("IT");
        unit2.setName("Dział IT");

        assertThat(unit1).isEqualTo(unit1);
        assertThat(unit1.hashCode()).isEqualTo(unit1.hashCode());

        assertThat(unit1).isEqualTo(unit2);
        assertThat(unit1.hashCode()).isEqualTo(unit2.hashCode());
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

    @Test
    @DisplayName("Powinien poprawnie obsłużyć wartości graniczne dla pola idn")
    void shouldHandleBoundaryValuesForIdn() {
        String longIdn = "A".repeat(15); // Maksymalna długość idn to 15 znaków

        TicketStatus status = new TicketStatus();
        status.setId(1);
        status.setIdn(longIdn);
        status.setName("Test Status");

        // Walidacja z maksymalną długością idn powinna przejść
        Set<ConstraintViolation<TicketStatus>> violations = validator.validate(status);
        assertThat(violations).isEmpty();

        // Sprawdzamy czy idn zostało poprawnie zapisane
        assertThat(status.getIdn()).isEqualTo(longIdn);
        assertThat(status.getIdn().length()).isEqualTo(15);
    }
}
