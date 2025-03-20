package adrianles.usww.service.impl;

import adrianles.usww.api.dto.dictionary.UserGroupDTO;
import adrianles.usww.api.mapper.dictionary.UserGroupMapper;
import adrianles.usww.domain.entity.dictionary.UserGroup;
import adrianles.usww.domain.repository.dictionary.UserGroupRepository;
import adrianles.usww.exception.ResourceNotFoundException;
import adrianles.usww.service.facade.UserGroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserGroupServiceImpl implements UserGroupService {
    private final UserGroupRepository userGroupRepository;
    private final UserGroupMapper userGroupMapper;

    @Override
    public List<UserGroupDTO> getAllUserGroups() {
        return userGroupRepository.findAll().stream()
                .map(userGroupMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserGroupDTO getUserGroupById(Integer id) {
        UserGroup userGroup = userGroupRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User group with id " + id + " not found"));
        return userGroupMapper.toDto(userGroup);
    }

    @Override
    public UserGroupDTO getUserGroupByIdn(String idn) {
        UserGroup userGroup = userGroupRepository.findByIdn(idn)
                .orElseThrow(() -> new ResourceNotFoundException("User group with idn " + idn + " not found"));
        return userGroupMapper.toDto(userGroup);
    }

}
