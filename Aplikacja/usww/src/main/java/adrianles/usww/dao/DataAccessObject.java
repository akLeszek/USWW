package adrianles.usww.dao;

import java.util.List;
import java.util.Optional;

public interface DataAccessObject<T> {
    Optional<T> get(Integer id);

    List<T> getAll();

    void save(T t);

    void update(T t, String[] params);

    void delete(T t);
}
