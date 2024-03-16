package adrianles.usww.controller;

import adrianles.usww.entity.MessageAttachment;
import adrianles.usww.service.AbstractService;
import org.springframework.stereotype.Controller;

@Controller(value = "/attachments")
public class MessageAttachmentController extends AbstractController<MessageAttachment> {
    public MessageAttachmentController(AbstractService<MessageAttachment> service) {
        super(service);
    }
}
