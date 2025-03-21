package adrianles.usww.unit.utils;

import adrianles.usww.config.MultipartConfig;
import adrianles.usww.service.util.FileValidator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class FileValidatorTest {

    @Mock
    private MultipartFile multipartFile;

    @Test
    @DisplayName("Should validate file successfully when all validations pass")
    void validateFile_shouldSucceedWhenFileIsValid() {
        when(multipartFile.isEmpty()).thenReturn(false);
        when(multipartFile.getSize()).thenReturn(1024L);
        when(multipartFile.getContentType()).thenReturn("application/pdf");

        assertDoesNotThrow(() -> FileValidator.validateFile(multipartFile));
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when file is null")
    void validateFile_shouldThrowExceptionWhenFileIsNull() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> FileValidator.validateFile(null));
        assertEquals("File is empty", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when file is empty")
    void validateFile_shouldThrowExceptionWhenFileIsEmpty() {
        when(multipartFile.isEmpty()).thenReturn(true);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> FileValidator.validateFile(multipartFile));
        assertEquals("File is empty", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when file size exceeds limit")
    void validateFile_shouldThrowExceptionWhenFileSizeExceedsLimit() {
        when(multipartFile.isEmpty()).thenReturn(false);
        when(multipartFile.getSize()).thenReturn(MultipartConfig.MAX_FILE_SIZE + 1);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> FileValidator.validateFile(multipartFile));
        assertEquals("File size is over limited to " + MultipartConfig.MAX_FILE_SIZE + " bytes", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when content type is null")
    void validateFile_shouldThrowExceptionWhenContentTypeIsNull() {
        when(multipartFile.isEmpty()).thenReturn(false);
        when(multipartFile.getSize()).thenReturn(1024L);
        when(multipartFile.getContentType()).thenReturn(null);
        when(multipartFile.getOriginalFilename()).thenReturn("test.txt");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> FileValidator.validateFile(multipartFile));
        assertTrue(exception.getMessage().startsWith("Unsupported content type: null"));
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when content type is not allowed")
    void validateFile_shouldThrowExceptionWhenContentTypeIsNotAllowed() {
        String unsupportedContentType = "text/plain";
        when(multipartFile.isEmpty()).thenReturn(false);
        when(multipartFile.getSize()).thenReturn(1024L);
        when(multipartFile.getContentType()).thenReturn(unsupportedContentType);
        when(multipartFile.getOriginalFilename()).thenReturn("test.txt");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> FileValidator.validateFile(multipartFile));
        assertTrue(exception.getMessage().startsWith("Unsupported content type: " + unsupportedContentType));
    }
}
