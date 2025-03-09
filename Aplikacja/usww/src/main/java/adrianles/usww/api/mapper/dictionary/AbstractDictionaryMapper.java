package adrianles.usww.api.mapper.dictionary;

import adrianles.usww.api.dto.dictionary.AbstractDictionaryDTO;
import adrianles.usww.domain.entity.dictionary.AbstractDictionary;

public abstract class AbstractDictionaryMapper<E extends AbstractDictionary, D extends AbstractDictionaryDTO> {

    public D toDto(E entity) {
        if (entity == null) {
            return null;
        }

        D dto = createDto(entity.getId(), entity.getIdn(), entity.getName());
        mapSpecificFields(entity, dto);
        return dto;
    }

    protected abstract D createDto(Integer id, String idn, String name);

    /**
     * Default done nothing.
     * Override in subclass when needed.
     *
     * @param entity
     * @param dto
     */
    protected void mapSpecificFields(E entity, D dto) {
    }

}
