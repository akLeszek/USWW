package adrianles.usww.service.impl;

import adrianles.usww.api.dto.UserDTO;
import adrianles.usww.api.mapper.UserMapper;
import adrianles.usww.domain.entity.User;
import adrianles.usww.domain.entity.dictionary.OrganizationUnit;
import adrianles.usww.domain.entity.dictionary.UserGroup;
import adrianles.usww.domain.repository.UserRepository;
import adrianles.usww.domain.repository.dictionary.OrganizationUnitRepository;
import adrianles.usww.domain.repository.dictionary.UserGroupRepository;
import adrianles.usww.exception.ResourceNotFoundException;
import adrianles.usww.service.facade.UserService;
import adrianles.usww.utils.UserGroupUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {


    private final UserRepository userRepository;
    private final OrganizationUnitRepository organizationUnitRepository;
    private final UserGroupRepository userGroupRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    @Override
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDTO getUserById(Integer id) {
        User user = findUserById(id);
        return userMapper.toDto(user);
    }

    @Override
    public UserDTO getUserByLogin(String login) {
        User user = userRepository.findByLogin(login)
                .orElseThrow(() -> new ResourceNotFoundException("Użytkownik o loginie " + login + " nie istnieje"));
        return userMapper.toDto(user);
    }

    @Override
    public UserDTO createUser(UserDTO userDTO) {
        validateUserData(userDTO);

        User user = new User();
        user.setLogin(userDTO.getLogin());

        user.setPassword(passwordEncoder.encode(userDTO.getLogin()));
        user.setForename(userDTO.getForename());
        user.setSurname(userDTO.getSurname());
        user.setLoginBan(userDTO.isLoginBan());
        user.setArchive(userDTO.isArchive());
        user.setFirstLogin(true);
        user.setOrganizationUnit(getOrganizationUnit(userDTO));
        user.setUserGroup(getUserGroup(userDTO));

        User savedUser = userRepository.save(user);
        UserDTO savedUserDTO = userMapper.toDto(savedUser);
        savedUserDTO.setGeneratedPassword(userDTO.getLogin());
        return savedUserDTO;
    }

    private void validateUserData(UserDTO userDTO) {
        UserGroup userGroup = userGroupRepository.findById(userDTO.getGroupId())
                .orElseThrow(() -> new ResourceNotFoundException("User group " + userDTO.getGroupId() + " does not exist"));

        if (UserGroupUtils.requiresOrganizationUnit(userGroup) && userDTO.getOrganizationUnitId() == null) {
            throw new IllegalArgumentException("Organization unit is required for that user group");
        }
    }

    @Override
    public UserDTO getUserBasicInfo(Integer id) {
        User user = findUserById(id);
        return userMapper.toDto(user);
    }

    private OrganizationUnit getOrganizationUnit(UserDTO userDTO) {
        if (userDTO.getOrganizationUnitId() == null) {
            return null;
        }

        return organizationUnitRepository.findById(userDTO.getOrganizationUnitId())
                .orElseThrow(() -> new ResourceNotFoundException("Jednostka organizacyjna nie istnieje"));
    }

    private UserGroup getUserGroup(UserDTO userDTO) {
        return userGroupRepository.findById(userDTO.getGroupId())
                .orElseThrow(() -> new ResourceNotFoundException("Grupa użytkowników nie istnieje"));
    }

    protected User findUserById(Integer id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Użytkownik o id " + id + " nie istnieje"));
    }
}
