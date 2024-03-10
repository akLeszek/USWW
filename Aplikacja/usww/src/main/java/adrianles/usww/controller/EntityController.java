package adrianles.usww.controller;

import adrianles.usww.EntityRepositoryService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;

public abstract class EntityController<T> {

    private EntityRepositoryService<T> service;

    EntityController(EntityRepositoryService service) {
        this.service = service;
    }

    @PostMapping("/index.json")
    public T create(@RequestBody T item) throws Exception {
        return service.create(item);
    }

    @PutMapping(value = "/{id}.json")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public T update(@PathVariable Integer id, @RequestBody T item) throws Exception {
        return service.update(item, new HashMap<>());
    }

    @GetMapping(value = "/{id}.json")
    public T read(@PathVariable Integer id) throws Exception {
        return service.get(id);
    }

    @GetMapping(value = "/index.json")
    public List<T> readAll() throws Exception {
        return service.findAll();
    }

    @DeleteMapping(value = "/{id}.json")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Integer id) throws Exception {
        service.deleteById(id);
    }
}
