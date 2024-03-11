package adrianles.usww.controller;

import adrianles.usww.entity.dictionary.Dictionary;
import adrianles.usww.service.AbstractService;
import org.springframework.stereotype.Controller;

@Controller(value = "/dictionaries")
public class DictionaryController extends AbstractController<Dictionary> {
    public DictionaryController(AbstractService<Dictionary> service) {
        super(service);
    }
}
