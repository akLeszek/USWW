package adrianles.usww.controller;

import adrianles.usww.entity.AbstractEntity;
import adrianles.usww.service.AbstractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

public abstract class AbstractController<E extends AbstractEntity> {

    private AbstractService<E> service;

    @Autowired
    public AbstractController(AbstractService<E> service) {
        this.service = service;
    }

    @PostMapping("/index.json")
    public E create(@RequestBody E item) throws Exception {
        return service.create(item);
    }

    @PutMapping(value = "/{id}.json")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public E update(@PathVariable Integer id, @RequestBody E item)
            throws Exception {
        return service.update(item);
    }

    @GetMapping(value = "/{id}.json")
    public E read(@PathVariable Integer id) throws Exception {
        return service.get(id);
    }

    @GetMapping(value = "/index.json")
    public List<E> readAll() throws Exception {
        return service.findAll();
    }

    @DeleteMapping(value = "/{id}.json")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Integer id) throws Exception {
        service.deleteById(id);
    }
}
