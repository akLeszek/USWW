package adrianles.usww.api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AuthResponse {
    private Integer userId;
    private String username;
    private String token;
}
