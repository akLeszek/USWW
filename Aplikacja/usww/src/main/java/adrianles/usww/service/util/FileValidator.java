package adrianles.usww.service.util;

import adrianles.usww.config.MultipartConfig;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;

public class FileValidator {

    public static void validateFile(MultipartFile file) {
        validateFileSize(file);
        validateContentType(file);
    }

    private static void validateFileSize(MultipartFile file) {
        if (file.getSize() > MultipartConfig.MAX_FILE_SIZE) {
            throw new IllegalArgumentException("File size is over limited to " + MultipartConfig.MAX_FILE_SIZE + " bytes");
        }
    }

    private static void validateContentType(MultipartFile file) {
        String contentType = file.getContentType();
        if (contentType == null || !Arrays.asList(MultipartConfig.ALLOWED_CONTENT_TYPES).contains(contentType)) {
            throw new IllegalArgumentException("Unsupported content type: " + contentType + " for file: " + file.getOriginalFilename() + ". Supported content types: " + Arrays.asList(MultipartConfig.ALLOWED_CONTENT_TYPES));
        }
    }
}
