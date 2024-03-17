package adrianles.usww.service;

import adrianles.usww.entity.TicketMessage;
import adrianles.usww.repository.CommonRepository;
import org.springframework.stereotype.Service;

@Service
public class TicketMessageService extends AbstractService<TicketMessage> {
    public TicketMessageService(CommonRepository<TicketMessage> repository) {
        super(repository);
    }

    @Override
    void verifyNew(TicketMessage entity) {

    }

    @Override
    void verifyUpdate(TicketMessage entity) {

    }

    @Override
    void verifyDelete(TicketMessage entity) {

    }

    @Override
    void archiveLinkedObjects(TicketMessage entity) {

    }

    @Override
    void archiveObject(TicketMessage entity) {

    }
}
