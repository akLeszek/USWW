package adrianles.usww;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Base64;
import java.util.Iterator;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserRepository userRepository;

    private UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping
    private List<ResponseEntity<User>> findAllUsers() {
        List<ResponseEntity<User>> users = new ArrayList<>();
        Iterator<User> userIterator = userRepository.findAll().iterator();
        while (userIterator.hasNext()) {
            User user = userIterator.next();
            ResponseEntity<User> userResponseEntity = ResponseEntity.ok(user);
            users.add(userResponseEntity);
        }
        return users;
    }

    @GetMapping("/{userId}")
    private ResponseEntity<User> findByUserId(@PathVariable Long userId) {
        if (userId.equals(99L)) {
            User user = new User(99L, "test", "12345");
            return ResponseEntity.ok(user);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
