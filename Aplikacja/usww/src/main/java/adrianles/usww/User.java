package adrianles.usww;

import org.springframework.data.annotation.Id;

public record User(
        @Id Long id,
        String login,
        String password
) {
}
