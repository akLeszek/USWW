package adrianles.usww.api.controller.common;

import adrianles.usww.config.MultipartConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/config")
@RequiredArgsConstructor
public class ConfigController {

    @GetMapping("/upload")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Object>> getUploadConfig() {
        Map<String, Object> config = new HashMap<>();
        config.put("maxFileSize", MultipartConfig.MAX_FILE_SIZE);
        config.put("maxRequestSize", MultipartConfig.MAX_REQUEST_SIZE);
        config.put("allowedContentTypes", MultipartConfig.ALLOWED_CONTENT_TYPES);

        return ResponseEntity.ok(config);
    }
}
