package adrianles.usww.api.mapper.dictionary;

import adrianles.usww.api.dto.dictionary.AbstractDictionaryDTO;
import adrianles.usww.domain.entity.dictionary.AbstractDictionary;

import java.util.List;
import java.util.stream.Collectors;

public interface DictionaryMapper<E extends AbstractDictionary, D extends AbstractDictionaryDTO> {
    D toDTO(E entity);

    default List<D> toDtoList(List<E> entities) {
        return entities.stream().map(this::toDTO).collect(Collectors.toList());
    }
}
