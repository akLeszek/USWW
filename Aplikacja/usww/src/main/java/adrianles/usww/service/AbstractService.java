package adrianles.usww.service;

import adrianles.usww.entity.AbstractEntity;
import adrianles.usww.repository.CommonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

public abstract class AbstractService<E extends AbstractEntity> {
    protected final CommonRepository<E> repository;

    @Autowired
    public AbstractService(CommonRepository<E> repository) {
        this.repository = repository;
    }


    public E get(Integer id) {
        return repository.findById(id).orElse(null);
    }

    public List<E> findAll() {
        return repository.findAll();
    }

    public List<E> findAllById(Integer id) {
        return repository.findAllById(Arrays.asList(id));
    }

    private E save(E entity, ServiceOperations operation) {
        verify(entity, operation);
        return repository.save(entity);
    }

    public E create(E entity) {
        return save(entity, ServiceOperations.NEW);
    }

    public E update(E entity) {
        return save(entity, ServiceOperations.UPDATE);
    }

    public void delete(E entity) {
        archiveLinkedObjects(entity);
        archiveObject(entity);
        save(entity, ServiceOperations.DELETE);
    }

    public void deleteById(Integer id) {
        E t = get(id);
        delete(t);
    }

    private void verify(E entity, ServiceOperations operation) {
        switch (operation) {
            case NEW -> verifyNew(entity);
            case UPDATE -> verifyUpdate(entity);
            case DELETE -> verifyDelete(entity);
        }
    }

    abstract void verifyNew(E entity);

    abstract void verifyUpdate(E entity);

    abstract void verifyDelete(E entity);

    abstract void archiveLinkedObjects(E entity);

    abstract void archiveObject(E entity);
}
