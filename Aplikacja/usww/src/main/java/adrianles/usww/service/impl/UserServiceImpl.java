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
import adrianles.usww.exception.UnauthorizedAccessException;
import adrianles.usww.security.authorization.AuthorizationService;
import adrianles.usww.security.userdetails.ExtendedUserDetails;
import adrianles.usww.service.facade.UserService;
import adrianles.usww.utils.Constants;
import adrianles.usww.utils.UserGroupUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
    private final AuthorizationService authorizationService;

    @Override
    public List<UserDTO> getAllUsers() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        ExtendedUserDetails userDetails = (ExtendedUserDetails) authentication.getPrincipal();

        List<User> users;

        if (userDetails.isAdmin()) {
            users = userRepository.findAll();
        } else if (userDetails.isOperator()) {
            Integer organizationUnitId = getOperatorOrganizationUnitId(userDetails.getUserId());
            if (organizationUnitId != null) {
                users = userRepository.findByOrganizationUnitId(organizationUnitId);
            } else {
                users = List.of();
            }
        } else {
            users = List.of(findUserById(userDetails.getUserId()));
        }

        return users.stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDTO getUserById(Integer userId) {
        User user = findUserById(userId);
        checkGetUserAuthorization(userId);
        return userMapper.toDto(user);
    }

    private void checkGetUserAuthorization(Integer userId) {
        if (!authorizationService.canAccessUser(userId)) {
            throw new UnauthorizedAccessException("Unauthorized access to view this user profile");
        }
    }

    @Override
    public UserDTO getUserByLogin(String login) {
        User user = userRepository.findByLogin(login)
                .orElseThrow(() -> new ResourceNotFoundException("User with login " + login + " not found"));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        ExtendedUserDetails userDetails = (ExtendedUserDetails) authentication.getPrincipal();

        if (user.getLogin().equals(userDetails.getUsername())) {
            return userMapper.toDto(user);
        }

        checkGetUserAuthorization(user.getId());
        return userMapper.toDto(user);
    }

    @Override
    public UserDTO createUser(UserDTO userDTO) {
        validateCreationPermissions(userDTO);
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

    @Override
    public UserDTO updateUser(Integer userId, UserDTO userDTO) {
        User user = findUserById(userId);
        checkModifyUserAuthorization(userId);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        ExtendedUserDetails currentUser = (ExtendedUserDetails) authentication.getPrincipal();

        if (!currentUser.isAdmin() && userDTO.getGroupId() != null &&
                !user.getUserGroup().getId().equals(userDTO.getGroupId())) {
            throw new UnauthorizedAccessException("Only administrators can update user groups");
        }

        user.setForename(userDTO.getForename());
        user.setSurname(userDTO.getSurname());

        if (currentUser.isAdmin()) {
            user.setLoginBan(userDTO.isLoginBan());
            user.setArchive(userDTO.isArchive());

            if (userDTO.getGroupId() != null) {
                user.setUserGroup(getUserGroup(userDTO));
            }

            if (userDTO.getOrganizationUnitId() != null) {
                user.setOrganizationUnit(getOrganizationUnit(userDTO));
            }
        }

        User savedUser = userRepository.save(user);
        return userMapper.toDto(savedUser);
    }

    private void checkModifyUserAuthorization(Integer userId) {
        if (!authorizationService.canModifyUser(userId)) {
            throw new UnauthorizedAccessException("Unauthorized access to modify user");
        }
    }

    @Override
    public UserDTO getUserBasicInfo(Integer id) {
        User user = findUserById(id);

        return userMapper.toBasicInfoDto(user);
    }

    private Integer getOperatorOrganizationUnitId(Integer operatorId) {
        User operator = findUserById(operatorId);
        return operator.getOrganizationUnit() != null ? operator.getOrganizationUnit().getId() : null;
    }

    private void validateCreationPermissions(UserDTO userDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        ExtendedUserDetails userDetails = (ExtendedUserDetails) authentication.getPrincipal();

        if (!userDetails.isAdmin()) {
            throw new UnauthorizedAccessException("Only administrators can create users");
        }
    }

    private void validateUserData(UserDTO userDTO) {
        UserGroup userGroup = userGroupRepository.findById(userDTO.getGroupId())
                .orElseThrow(() -> new ResourceNotFoundException("User group " + userDTO.getGroupId() + " does not exist"));

        if (UserGroupUtils.requiresOrganizationUnit(userGroup) && userDTO.getOrganizationUnitId() == null) {
            throw new IllegalArgumentException("Organization unit is required for that user group");
        }

        if (userRepository.findByLogin(userDTO.getLogin()).isPresent()) {
            throw new IllegalArgumentException("User with login " + userDTO.getLogin() + " already exists");
        }
    }

    private OrganizationUnit getOrganizationUnit(UserDTO userDTO) {
        if (userDTO.getOrganizationUnitId() == null) {
            return null;
        }

        return organizationUnitRepository.findById(userDTO.getOrganizationUnitId())
                .orElseThrow(() -> new ResourceNotFoundException("Organization unit " + userDTO.getOrganizationUnitId() + " does not exist"));
    }

    private UserGroup getUserGroup(UserDTO userDTO) {
        return userGroupRepository.findById(userDTO.getGroupId())
                .orElseThrow(() -> new ResourceNotFoundException("User group " + userDTO.getGroupId() + " does not exist"));
    }

    protected User findUserById(Integer id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User with id " + id + " does not exist"));
    }

    @Override
    public List<UserDTO> getAllOperators() {
        List<User> operators = userRepository.findAllByUserGroupIdn(Constants.OPERATOR_GROUP_IDN);
        return operators.stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());
    }
}
