package adrianles.usww.controller;

import adrianles.usww.entity.User;
import adrianles.usww.service.AbstractService;
import org.springframework.stereotype.Controller;

//@Controller(value = "/users")
public class UserController extends AbstractController<User> {
    public UserController(AbstractService<User> service) {
        super(service);
    }
}
