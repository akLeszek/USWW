package adrianles.usww.api.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MessageAttachmentDTO {
    private Integer id;
    private Integer messageId;
    private String fileName;
    private byte[] attachment;
}
