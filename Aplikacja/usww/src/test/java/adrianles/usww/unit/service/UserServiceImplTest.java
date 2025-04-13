package adrianles.usww.unit.service;

import adrianles.usww.api.dto.UserDTO;
import adrianles.usww.api.mapper.UserMapper;
import adrianles.usww.domain.entity.User;
import adrianles.usww.domain.entity.dictionary.OrganizationUnit;
import adrianles.usww.domain.entity.dictionary.UserGroup;
import adrianles.usww.domain.repository.UserRepository;
import adrianles.usww.domain.repository.dictionary.OrganizationUnitRepository;
import adrianles.usww.domain.repository.dictionary.UserGroupRepository;
import adrianles.usww.exception.ResourceNotFoundException;
import adrianles.usww.security.authorization.AuthorizationService;
import adrianles.usww.security.userdetails.ExtendedUserDetails;
import adrianles.usww.service.impl.UserServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserGroupRepository userGroupRepository;

    @Mock
    private OrganizationUnitRepository organizationUnitRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserMapper userMapper;

    @Mock
    private AuthorizationService authorizationService;

    @Mock
    private ExtendedUserDetails userDetails;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;
    private UserDTO userDTO;
    private UserGroup adminGroup;
    private OrganizationUnit organizationUnit;
    private Authentication authentication;

    @BeforeEach
    void setUp() {
        // Tworzymy przykładowe obiekty używane w testach
        adminGroup = new UserGroup();
        adminGroup.setId(1);
        adminGroup.setIdn("ADMIN");
        adminGroup.setName("Administratorzy");

        organizationUnit = new OrganizationUnit();
        organizationUnit.setId(1);
        organizationUnit.setIdn("IT");
        organizationUnit.setName("Dział IT");

        user = new User();
        user.setId(1);
        user.setLogin("testuser");
        user.setPassword("encodedPassword");
        user.setForename("Jan");
        user.setSurname("Kowalski");
        user.setUserGroup(adminGroup);
        user.setOrganizationUnit(organizationUnit);

        userDTO = new UserDTO();
        userDTO.setId(1);
        userDTO.setLogin("testuser");
        userDTO.setForename("Jan");
        userDTO.setSurname("Kowalski");
        userDTO.setGroupId(1);
        userDTO.setOrganizationUnitId(1);

        // Konfiguracja SecurityContext dla testów
        when(userDetails.getUserId()).thenReturn(1);
        when(userDetails.isAdmin()).thenReturn(true);
        when(userDetails.getUsername()).thenReturn("testuser");

        authentication = new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                Collections.singletonList(new SimpleGrantedAuthority("ADMIN"))
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("Powinien zwrócić listę wszystkich użytkowników")
    void getAllUsers_shouldReturnAllUsers() {
        // given
        User user2 = new User();
        user2.setId(2);
        List<User> users = Arrays.asList(user, user2);

        UserDTO userDTO2 = new UserDTO();
        userDTO2.setId(2);
        List<UserDTO> userDTOs = Arrays.asList(userDTO, userDTO2);

        when(userRepository.findAll()).thenReturn(users);
        when(userMapper.toDto(user)).thenReturn(userDTO);
        when(userMapper.toDto(user2)).thenReturn(userDTO2);

        // when
        List<UserDTO> result = userService.getAllUsers();

        // then
        assertThat(result).hasSize(2);
        assertThat(result).isEqualTo(userDTOs);
        verify(userRepository).findAll();
        verify(userMapper, times(2)).toDto(any(User.class));
    }

    @Test
    @DisplayName("Powinien zwrócić użytkownika po ID")
    void getUserById_shouldReturnUserById() {
        // given
        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(userMapper.toDto(user)).thenReturn(userDTO);
        when(authorizationService.canAccessUser(1)).thenReturn(true);

        // when
        UserDTO result = userService.getUserById(1);

        // then
        assertThat(result).isEqualTo(userDTO);
        verify(userRepository).findById(1);
        verify(userMapper).toDto(user);
        verify(authorizationService).canAccessUser(1);
    }

    @Test
    @DisplayName("Powinien rzucić wyjątek gdy nie znaleziono użytkownika po ID")
    void getUserById_shouldThrowExceptionWhenUserNotFound() {
        // given
        when(userRepository.findById(99)).thenReturn(Optional.empty());

        // when / then
        assertThatThrownBy(() -> userService.getUserById(99))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("User with id 99 does not exist");

        verify(userRepository).findById(99);
        verifyNoInteractions(userMapper);
    }

    @Test
    @DisplayName("Powinien zwrócić użytkownika po loginie")
    void getUserByLogin_shouldReturnUserByLogin() {
        // given
        when(userRepository.findByLogin("testuser")).thenReturn(Optional.of(user));
        when(userMapper.toDto(user)).thenReturn(userDTO);
        when(authorizationService.canAccessUser(1)).thenReturn(true);

        // when
        UserDTO result = userService.getUserByLogin("testuser");

        // then
        assertThat(result).isEqualTo(userDTO);
        verify(userRepository).findByLogin("testuser");
        verify(userMapper).toDto(user);
    }

    @Test
    @DisplayName("Powinien rzucić wyjątek gdy nie znaleziono użytkownika po loginie")
    void getUserByLogin_shouldThrowExceptionWhenUserNotFound() {
        // given
        when(userRepository.findByLogin("nonexistent")).thenReturn(Optional.empty());

        // when / then
        assertThatThrownBy(() -> userService.getUserByLogin("nonexistent"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("User with login nonexistent not found");

        verify(userRepository).findByLogin("nonexistent");
        verifyNoInteractions(userMapper);
    }

    @Test
    @DisplayName("Powinien utworzyć nowego użytkownika")
    void createUser_shouldCreateNewUser() {
        // given
        UserDTO newUserDTO = new UserDTO();
        newUserDTO.setLogin("newuser");
        newUserDTO.setForename("Anna");
        newUserDTO.setSurname("Nowak");
        newUserDTO.setGroupId(1);
        newUserDTO.setOrganizationUnitId(1);

        User newUser = new User();
        newUser.setLogin("newuser");
        newUser.setForename("Anna");
        newUser.setSurname("Nowak");

        User savedUser = new User();
        savedUser.setId(2);
        savedUser.setLogin("newuser");
        savedUser.setForename("Anna");
        savedUser.setSurname("Nowak");
        savedUser.setUserGroup(adminGroup);
        savedUser.setOrganizationUnit(organizationUnit);

        UserDTO savedUserDTO = new UserDTO();
        savedUserDTO.setId(2);
        savedUserDTO.setLogin("newuser");
        savedUserDTO.setForename("Anna");
        savedUserDTO.setSurname("Nowak");
        savedUserDTO.setGroupId(1);
        savedUserDTO.setOrganizationUnitId(1);

        when(userGroupRepository.findById(1)).thenReturn(Optional.of(adminGroup));
        when(organizationUnitRepository.findById(1)).thenReturn(Optional.of(organizationUnit));
        when(passwordEncoder.encode("newuser")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(userMapper.toDto(savedUser)).thenReturn(savedUserDTO);
        when(authorizationService.hasRole("ADMIN")).thenReturn(true);

        // when
        UserDTO result = userService.createUser(newUserDTO);

        // then
        assertThat(result).isEqualTo(savedUserDTO);
        assertThat(result.getGeneratedPassword()).isEqualTo("newuser");

        verify(userGroupRepository, times(2)).findById(1);
        verify(organizationUnitRepository).findById(1);
        verify(passwordEncoder).encode("newuser");
        verify(userRepository).save(any(User.class));
        verify(userMapper).toDto(savedUser);
    }

    @Test
    @DisplayName("Powinien rzucić wyjątek gdy grupa użytkownika nie istnieje")
    void createUser_shouldThrowExceptionWhenUserGroupNotFound() {
        // given
        UserDTO newUserDTO = new UserDTO();
        newUserDTO.setLogin("newuser");
        newUserDTO.setGroupId(99); // Nieistniejąca grupa

        when(authorizationService.hasRole("ADMIN")).thenReturn(true);
        when(userGroupRepository.findById(99)).thenReturn(Optional.empty());

        // when / then
        assertThatThrownBy(() -> userService.createUser(newUserDTO))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("User group 99 does not exist");

        verify(userGroupRepository).findById(99);
        verifyNoInteractions(passwordEncoder, userMapper);
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Powinien rzucić wyjątek gdy jednostka organizacyjna nie istnieje")
    void createUser_shouldThrowExceptionWhenOrganizationUnitNotFound() {
        // given
        UserDTO newUserDTO = new UserDTO();
        newUserDTO.setLogin("newuser");
        newUserDTO.setGroupId(1);
        newUserDTO.setOrganizationUnitId(99); // Nieistniejąca jednostka

        when(authorizationService.hasRole("ADMIN")).thenReturn(true);
        when(userGroupRepository.findById(1)).thenReturn(Optional.of(adminGroup));
        when(organizationUnitRepository.findById(99)).thenReturn(Optional.empty());

        // when / then
        assertThatThrownBy(() -> userService.createUser(newUserDTO))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Organization unit 99 does not exist");

        verify(userGroupRepository).findById(1);
        verify(organizationUnitRepository).findById(99);
        verify(userRepository, never()).save(any());
        verify(userMapper, never()).toDto(any());
    }

    @Test
    @DisplayName("Powinien zwrócić podstawowe informacje o użytkowniku")
    void getUserBasicInfo_shouldReturnBasicUserInfo() {
        // given
        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(userMapper.toBasicInfoDto(user)).thenReturn(userDTO);

        // when
        UserDTO result = userService.getUserBasicInfo(1);

        // then
        assertThat(result).isEqualTo(userDTO);
        verify(userRepository).findById(1);
        verify(userMapper).toBasicInfoDto(user);
    }
}
