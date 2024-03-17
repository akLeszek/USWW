package adrianles.usww.service;

import adrianles.usww.entity.Ticket;
import adrianles.usww.repository.CommonRepository;
import org.springframework.stereotype.Service;

@Service
public class TicketService extends AbstractService<Ticket> {
    public TicketService(CommonRepository<Ticket> repository) {
        super(repository);
    }

    @Override
    void verifyNew(Ticket t) {

    }

    @Override
    void verifyUpdate(Ticket t) {

    }

    @Override
    void verifyDelete(Ticket t) {

    }

    @Override
    void archiveLinkedObjects(Ticket t) {

    }

    @Override
    void archiveObject(Ticket t) {

    }
}
