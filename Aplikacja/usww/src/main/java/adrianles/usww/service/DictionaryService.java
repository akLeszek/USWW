package adrianles.usww.service;

import adrianles.usww.entity.dictionary.Dictionary;
import adrianles.usww.repository.CommonRepository;
import org.springframework.stereotype.Service;

@Service
public class DictionaryService extends AbstractService<Dictionary> {

    public DictionaryService(CommonRepository<Dictionary> repository) {
        super(repository);
    }

    @Override
    void verifyNew(Dictionary t) {
    }

    @Override
    void verifyUpdate(Dictionary t) {

    }

    @Override
    void verifyDelete(Dictionary t) {

    }

    @Override
    void archiveLinkedObjects(Dictionary t) {

    }

    @Override
    void archiveObject(Dictionary t) {

    }
}
