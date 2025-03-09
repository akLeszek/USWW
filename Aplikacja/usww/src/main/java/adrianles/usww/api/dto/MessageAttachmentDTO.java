package adrianles.usww.api.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MessageAttachmentDTO {
    private Integer id;
    private Integer messageId;
    private byte[] attachment;
}
