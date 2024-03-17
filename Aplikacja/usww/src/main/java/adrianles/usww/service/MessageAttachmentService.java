package adrianles.usww.service;

import adrianles.usww.entity.MessageAttachment;
import adrianles.usww.repository.CommonRepository;
import org.springframework.stereotype.Service;

@Service
public class MessageAttachmentService extends AbstractService<MessageAttachment>{
    public MessageAttachmentService(CommonRepository<MessageAttachment> repository) {
        super(repository);
    }

    @Override
    void verifyNew(MessageAttachment entity) {

    }

    @Override
    void verifyUpdate(MessageAttachment entity) {

    }

    @Override
    void verifyDelete(MessageAttachment entity) {

    }

    @Override
    void archiveLinkedObjects(MessageAttachment entity) {

    }

    @Override
    void archiveObject(MessageAttachment entity) {

    }
}
