package adrianles.usww.unit.domain.entity;

import adrianles.usww.domain.entity.AbstractEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class AbstractEntityTest {

    private static class TestEntity extends AbstractEntity {
    }

    @Test
    @DisplayName("Powinien poprawnie stworzyć obiekt i obsłużyć ID")
    void shouldCreateEntityAndHandleId() {
        TestEntity entity = new TestEntity();
        entity.setId(42);
        assertThat(entity.getId()).isEqualTo(42);
    }

    @Test
    @DisplayName("Powinien poprawnie obsłużyć konstruktor z parametrem")
    void shouldHandleParameterizedConstructor() {
        TestEntity entity = new TestEntity();
        entity.setId(100);
        assertThat(entity.getId()).isEqualTo(100);
    }

    @Test
    @DisplayName("Powinien poprawnie porównać dwa identyczne obiekty")
    void shouldCompareEqualObjects() {
        TestEntity entity1 = new TestEntity();
        entity1.setId(1);

        TestEntity entity2 = new TestEntity();
        entity2.setId(1);

        assertThat(entity1).isEqualTo(entity1);
        assertThat(entity1.hashCode()).isEqualTo(entity1.hashCode());

        assertThat(entity1).isEqualTo(entity2);
        assertThat(entity2).isEqualTo(entity1);

        assertThat(entity1.hashCode()).isEqualTo(entity2.hashCode());
    }

    @Test
    @DisplayName("Powinien poprawnie rozróżnić dwa różne obiekty")
    void shouldDifferentiateNonEqualObjects() {
        TestEntity entity1 = new TestEntity();
        entity1.setId(1);

        TestEntity entity2 = new TestEntity();
        entity2.setId(2);

        TestEntity nullIdEntity = new TestEntity();

        assertThat(entity1).isNotEqualTo(null);
        assertThat(entity1).isNotEqualTo(new Object());

        assertThat(entity1).isNotEqualTo(entity2);
        assertThat(entity1.hashCode()).isNotEqualTo(entity2.hashCode());

        assertThat(nullIdEntity).isNotEqualTo(entity1);
        assertThat(entity1).isNotEqualTo(nullIdEntity);

        TestEntity anotherNullIdEntity = new TestEntity();
        assertThat(nullIdEntity).isEqualTo(anotherNullIdEntity);
    }

    @Test
    @DisplayName("Powinien zachować tranzytywność porównania")
    void shouldMaintainTransitiveEquality() {
        TestEntity entity1 = new TestEntity();
        entity1.setId(5);

        TestEntity entity2 = new TestEntity();
        entity2.setId(5);

        TestEntity entity3 = new TestEntity();
        entity3.setId(5);

        assertThat(entity1).isEqualTo(entity2);
        assertThat(entity2).isEqualTo(entity3);
        assertThat(entity1).isEqualTo(entity3);
    }
}
