package adrianles.usww.service;

import adrianles.usww.entity.User;
import adrianles.usww.service.repository.CommonRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService extends AbstractService<User> {
    public UserService(CommonRepository<User> repository) {
        super(repository);
    }

    @Override
    void verifyNew(User t) {

    }

    @Override
    void verifyUpdate(User t) {

    }

    @Override
    void verifyDelete(User t) {

    }

    @Override
    void archiveLinkedObjects(User t) {

    }

    @Override
    void archiveObject(User t) {

    }
}
