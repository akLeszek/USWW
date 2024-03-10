package adrianles.usww;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public abstract class EntityRepositoryService<T> {
    private EntityRepository<T, Integer> entityRepository;

    EntityRepositoryService(EntityRepository entityRepository) {
        this.entityRepository = entityRepository;
    }

    public T get(Integer id) {
        return entityRepository.findById(id).orElse(null);
    }

    public List<T> findAll() {
        return entityRepository.findAll();
    }

    public List<T> findAllById(Integer id) {
        return entityRepository.findAllById(Arrays.asList(id));
    }

    private T save(T t, RepositoryOperations operation) {
        verify(t, operation);
        return entityRepository.save(t);
    }

    public T create(T t) {
        return save(t, RepositoryOperations.NEW);
    }

    public T update(T t, Map<String, Objects> params) {
        updateFields(t, params);
        return save(t, RepositoryOperations.UPDATE);
    }

    public void delete(T t) {
        archiveLinkedObjects(t);
        archiveObject(t);
        save(t, RepositoryOperations.DELETE);
    }

    public void deleteById(Integer id) {
        T t = get(id);
        delete(t);
    }

    abstract void verify(T t, RepositoryOperations operation);

    abstract void updateFields(T t, Map<String, Objects> params);

    abstract void archiveLinkedObjects(T t);

    abstract void archiveObject(T t);
}
