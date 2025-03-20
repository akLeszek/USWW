package adrianles.usww.unit.service.impl;

import adrianles.usww.api.dto.MessageAttachmentDTO;
import adrianles.usww.api.mapper.MessageAttachmentMapper;
import adrianles.usww.domain.entity.MessageAttachment;
import adrianles.usww.domain.entity.TicketMessage;
import adrianles.usww.domain.repository.MessageAttachmentRepository;
import adrianles.usww.domain.repository.TicketMessageRepository;
import adrianles.usww.exception.ResourceNotFoundException;
import adrianles.usww.service.impl.MessageAttachmentServiceImpl;
import adrianles.usww.service.util.FileValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MessageAttachmentServiceImplTest {

    @Mock
    private MessageAttachmentRepository messageAttachmentRepository;

    @Mock
    private TicketMessageRepository ticketMessageRepository;

    @Mock
    private MessageAttachmentMapper messageAttachmentMapper;

    @InjectMocks
    private MessageAttachmentServiceImpl messageAttachmentService;

    private MessageAttachment attachment1;
    private MessageAttachment attachment2;
    private MessageAttachmentDTO attachmentDTO1;
    private MessageAttachmentDTO attachmentDTO2;
    private TicketMessage ticketMessage;
    private MultipartFile multipartFile;

    @BeforeEach
    void setUp() {
        ticketMessage = new TicketMessage();
        ticketMessage.setId(1);

        attachment1 = new MessageAttachment();
        attachment1.setId(1);
        attachment1.setMessage(ticketMessage);
        attachment1.setFilename("document1.pdf");
        attachment1.setAttachment("content1".getBytes());

        attachment2 = new MessageAttachment();
        attachment2.setId(2);
        attachment2.setMessage(ticketMessage);
        attachment2.setFilename("document2.pdf");
        attachment2.setAttachment("content2".getBytes());

        attachmentDTO1 = new MessageAttachmentDTO(1, 1, "document1.pdf", "content1".getBytes());
        attachmentDTO2 = new MessageAttachmentDTO(2, 1, "document2.pdf", "content2".getBytes());

        multipartFile = new MockMultipartFile(
                "file",
                "test.pdf",
                "application/pdf",
                "test content".getBytes()
        );
    }

    @Test
    @DisplayName("getAllAttachmentsByMessageId powinno zwrócić listę załączników dla istniejącej wiadomości")
    void getAllAttachmentsByMessageId_ShouldReturnListOfAttachments() {
        // given
        List<MessageAttachment> attachments = Arrays.asList(attachment1, attachment2);
        when(messageAttachmentRepository.findByMessageId(1)).thenReturn(attachments);
        when(messageAttachmentMapper.toDto(attachment1)).thenReturn(attachmentDTO1);
        when(messageAttachmentMapper.toDto(attachment2)).thenReturn(attachmentDTO2);

        // when
        List<MessageAttachmentDTO> result = messageAttachmentService.getAllAttachmentsByMessageId(1);

        // then
        assertThat(result).hasSize(2);
        assertThat(result).containsExactly(attachmentDTO1, attachmentDTO2);
        verify(messageAttachmentRepository, times(1)).findByMessageId(1);
        verify(messageAttachmentMapper, times(1)).toDto(attachment1);
        verify(messageAttachmentMapper, times(1)).toDto(attachment2);
    }

    @Test
    @DisplayName("getAllAttachmentsByMessageId powinno zwrócić pustą listę gdy brak załączników")
    void getAllAttachmentsByMessageId_ShouldReturnEmptyList_WhenNoAttachmentsFound() {
        // given
        when(messageAttachmentRepository.findByMessageId(1)).thenReturn(Collections.emptyList());

        // when
        List<MessageAttachmentDTO> result = messageAttachmentService.getAllAttachmentsByMessageId(1);

        // then
        assertThat(result).isEmpty();
        verify(messageAttachmentRepository, times(1)).findByMessageId(1);
        verify(messageAttachmentMapper, never()).toDto(any());
    }

    @Test
    @DisplayName("getAttachmentById powinno zwrócić załącznik gdy istnieje")
    void getAttachmentById_ShouldReturnAttachment_WhenExists() {
        // given
        when(messageAttachmentRepository.findById(1)).thenReturn(Optional.of(attachment1));
        when(messageAttachmentMapper.toDto(attachment1)).thenReturn(attachmentDTO1);

        // when
        MessageAttachmentDTO result = messageAttachmentService.getAttachmentById(1);

        // then
        assertThat(result).isEqualTo(attachmentDTO1);
        verify(messageAttachmentRepository, times(1)).findById(1);
        verify(messageAttachmentMapper, times(1)).toDto(attachment1);
    }

    @Test
    @DisplayName("getAttachmentById powinno rzucić wyjątek gdy załącznik nie istnieje")
    void getAttachmentById_ShouldThrowException_WhenAttachmentNotFound() {
        // given
        when(messageAttachmentRepository.findById(999)).thenReturn(Optional.empty());

        // when
        Throwable thrown = catchThrowable(() -> messageAttachmentService.getAttachmentById(999));

        // then
        assertThat(thrown)
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Attachment not found with id: 999");
        verify(messageAttachmentRepository, times(1)).findById(999);
        verify(messageAttachmentMapper, never()).toDto(any());
    }

    @Test
    @DisplayName("createAttachment powinno utworzyć nowy załącznik")
    void createAttachment_ShouldCreateNewAttachment() throws IOException {
        // given
        when(ticketMessageRepository.findById(1)).thenReturn(Optional.of(ticketMessage));

        MessageAttachment savedAttachment = new MessageAttachment();
        savedAttachment.setId(1);
        savedAttachment.setMessage(ticketMessage);
        savedAttachment.setFilename("test.pdf");
        savedAttachment.setAttachment("test content".getBytes());

        when(messageAttachmentRepository.save(any(MessageAttachment.class))).thenReturn(savedAttachment);
        when(messageAttachmentMapper.toDto(savedAttachment)).thenReturn(attachmentDTO1);

        try (MockedStatic<FileValidator> fileValidator = Mockito.mockStatic(FileValidator.class)) {
            fileValidator.when(() -> FileValidator.validateFile(any())).then(invocation -> null);

            // when
            MessageAttachmentDTO result = messageAttachmentService.createAttachment(1, multipartFile);

            // then
            assertThat(result).isEqualTo(attachmentDTO1);
            verify(ticketMessageRepository, times(1)).findById(1);
            verify(messageAttachmentRepository, times(1)).save(any(MessageAttachment.class));
            verify(messageAttachmentMapper, times(1)).toDto(savedAttachment);
            fileValidator.verify(() -> FileValidator.validateFile(multipartFile), times(1));
        }
    }

    @Test
    @DisplayName("createAttachment powinno rzucić wyjątek gdy wiadomość nie istnieje")
    void createAttachment_ShouldThrowException_WhenMessageNotFound() throws IOException {
        // given
        when(ticketMessageRepository.findById(999)).thenReturn(Optional.empty());

        try (MockedStatic<FileValidator> fileValidator = Mockito.mockStatic(FileValidator.class)) {
            fileValidator.when(() -> FileValidator.validateFile(any())).then(invocation -> null);

            // when
            Throwable thrown = catchThrowable(() -> messageAttachmentService.createAttachment(999, multipartFile));

            // then
            assertThat(thrown)
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Message not found with id: 999");
            verify(ticketMessageRepository, times(1)).findById(999);
            verify(messageAttachmentRepository, never()).save(any());
            verify(messageAttachmentMapper, never()).toDto(any());
            fileValidator.verify(() -> FileValidator.validateFile(multipartFile), times(1));
        }
    }

    @Test
    @DisplayName("deleteAttachment powinno usunąć istniejący załącznik")
    void deleteAttachment_ShouldDeleteExistingAttachment() {
        // given
        when(messageAttachmentRepository.findById(1)).thenReturn(Optional.of(attachment1));
        doNothing().when(messageAttachmentRepository).delete(attachment1);

        // when
        messageAttachmentService.deleteAttachment(1);

        // then
        verify(messageAttachmentRepository, times(1)).findById(1);
        verify(messageAttachmentRepository, times(1)).delete(attachment1);
    }

    @Test
    @DisplayName("deleteAttachment powinno rzucić wyjątek gdy załącznik nie istnieje")
    void deleteAttachment_ShouldThrowException_WhenAttachmentNotFound() {
        // given
        when(messageAttachmentRepository.findById(999)).thenReturn(Optional.empty());

        // when
        Throwable thrown = catchThrowable(() -> messageAttachmentService.deleteAttachment(999));

        // then
        assertThat(thrown)
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Attachment not found with id: 999");
        verify(messageAttachmentRepository, times(1)).findById(999);
        verify(messageAttachmentRepository, never()).delete(any());
    }
}
