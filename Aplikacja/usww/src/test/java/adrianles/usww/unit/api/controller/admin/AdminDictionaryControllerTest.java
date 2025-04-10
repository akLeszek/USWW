package adrianles.usww.unit.api.controller.admin;

import adrianles.usww.api.controller.common.DictionaryController;
import adrianles.usww.api.dto.dictionary.*;
import adrianles.usww.service.facade.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminDictionaryControllerTest {

    @Mock
    private TicketCategoryService ticketCategoryService;

    @Mock
    private TicketStatusService ticketStatusService;

    @Mock
    private TicketPriorityDictionaryService ticketPriorityService;

    @Mock
    private UserGroupService userGroupService;

    @Mock
    private OrganizationUnitService organizationUnitService;

    @InjectMocks
    private DictionaryController dictionaryController;

    private TicketCategoryDTO categoryDTO1;
    private TicketCategoryDTO categoryDTO2;
    private List<TicketCategoryDTO> categories;

    private TicketStatusDTO statusDTO1;
    private TicketStatusDTO statusDTO2;
    private List<TicketStatusDTO> statuses;

    private TicketPriorityDTO priorityDTO1;
    private TicketPriorityDTO priorityDTO2;
    private List<TicketPriorityDTO> priorities;

    private UserGroupDTO userGroupDTO1;
    private UserGroupDTO userGroupDTO2;
    private List<UserGroupDTO> userGroups;

    private OrganizationUnitDTO orgUnitDTO1;
    private OrganizationUnitDTO orgUnitDTO2;
    private List<OrganizationUnitDTO> organizationUnits;

    @BeforeEach
    void setUp() {
        // Initialize test data for categories
        categoryDTO1 = new TicketCategoryDTO(1, "LOGOWANIE", "Problemy z logowaniem");
        categoryDTO2 = new TicketCategoryDTO(2, "KURS", "Problemy z kursami");
        categories = Arrays.asList(categoryDTO1, categoryDTO2);

        // Initialize test data for statuses
        statusDTO1 = new TicketStatusDTO(1, "NEW", "Nowe");
        statusDTO2 = new TicketStatusDTO(2, "IN_PROGRESS", "W trakcie");
        statuses = Arrays.asList(statusDTO1, statusDTO2);

        // Initialize test data for priorities
        priorityDTO1 = new TicketPriorityDTO(1, "LOW", "Niski");
        priorityDTO2 = new TicketPriorityDTO(2, "MEDIUM", "Średni");
        priorities = Arrays.asList(priorityDTO1, priorityDTO2);

        // Initialize test data for user groups
        userGroupDTO1 = new UserGroupDTO(1, "ADMIN", "Administratorzy");
        userGroupDTO2 = new UserGroupDTO(2, "STUDENT", "Studenci");
        userGroups = Arrays.asList(userGroupDTO1, userGroupDTO2);

        // Initialize test data for organization units
        orgUnitDTO1 = new OrganizationUnitDTO(1, "WH", "Wydział Humanistyczny");
        orgUnitDTO2 = new OrganizationUnitDTO(2, "WNP", "Wydział Nauk Przyrodniczych");
        organizationUnits = Arrays.asList(orgUnitDTO1, orgUnitDTO2);
    }

    @Test
    @DisplayName("Powinien zwrócić wszystkie kategorie zgłoszeń")
    void getAllCategories_shouldReturnAllCategories() {
        // Arrange
        when(ticketCategoryService.getAllTicketCategories()).thenReturn(categories);

        // Act
        ResponseEntity<List<TicketCategoryDTO>> response = dictionaryController.getAllCategories();

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).hasSize(2);
        assertThat(response.getBody()).contains(categoryDTO1, categoryDTO2);
        verify(ticketCategoryService).getAllTicketCategories();
    }

    @Test
    @DisplayName("Powinien zwrócić kategorię zgłoszenia po identyfikatorze")
    void getCategoryById_shouldReturnCategoryById() {
        // Arrange
        when(ticketCategoryService.getTicketCategoryById(1)).thenReturn(categoryDTO1);

        // Act
        ResponseEntity<TicketCategoryDTO> response = dictionaryController.getCategoryById(1);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).isEqualTo(categoryDTO1);
        verify(ticketCategoryService).getTicketCategoryById(1);
    }

    @Test
    @DisplayName("Powinien zwrócić kategorię zgłoszenia po identyfikatorze IDN")
    void getCategoryByIdn_shouldReturnCategoryByIdn() {
        // Arrange
        when(ticketCategoryService.getTicketCategoryByIdn("LOGOWANIE")).thenReturn(categoryDTO1);

        // Act
        ResponseEntity<TicketCategoryDTO> response = dictionaryController.getCategoryByIdn("LOGOWANIE");

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).isEqualTo(categoryDTO1);
        verify(ticketCategoryService).getTicketCategoryByIdn("LOGOWANIE");
    }

    @Test
    @DisplayName("Powinien zwrócić wszystkie statusy zgłoszeń")
    void getAllStatuses_shouldReturnAllStatuses() {
        // Arrange
        when(ticketStatusService.getAllTicketStatuses()).thenReturn(statuses);

        // Act
        ResponseEntity<List<TicketStatusDTO>> response = dictionaryController.getAllStatuses();

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).hasSize(2);
        assertThat(response.getBody()).contains(statusDTO1, statusDTO2);
        verify(ticketStatusService).getAllTicketStatuses();
    }

    @Test
    @DisplayName("Powinien zwrócić status zgłoszenia po identyfikatorze")
    void getTicketStatusById_shouldReturnStatusById() {
        // Arrange
        when(ticketStatusService.getTicketStatusById(1)).thenReturn(statusDTO1);

        // Act
        ResponseEntity<TicketStatusDTO> response = dictionaryController.getTicketStatusById(1);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).isEqualTo(statusDTO1);
        verify(ticketStatusService).getTicketStatusById(1);
    }

    @Test
    @DisplayName("Powinien zwrócić status zgłoszenia po identyfikatorze IDN")
    void getTicketStatusByIdn_shouldReturnStatusByIdn() {
        // Arrange
        when(ticketStatusService.getTicketStatusByIdn("NEW")).thenReturn(statusDTO1);

        // Act
        ResponseEntity<TicketStatusDTO> response = dictionaryController.getTicketStatusByIdn("NEW");

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).isEqualTo(statusDTO1);
        verify(ticketStatusService).getTicketStatusByIdn("NEW");
    }

    @Test
    @DisplayName("Powinien zwrócić wszystkie priorytety zgłoszeń")
    void getAllPriorities_shouldReturnAllPriorities() {
        // Arrange
        when(ticketPriorityService.getAllTicketPriorities()).thenReturn(priorities);

        // Act
        ResponseEntity<List<TicketPriorityDTO>> response = dictionaryController.getAllPriorities();

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).hasSize(2);
        assertThat(response.getBody()).contains(priorityDTO1, priorityDTO2);
        verify(ticketPriorityService).getAllTicketPriorities();
    }

    @Test
    @DisplayName("Powinien zwrócić wszystkie grupy użytkowników")
    void getAllUserGroups_shouldReturnAllUserGroups() {
        // Arrange
        when(userGroupService.getAllUserGroups()).thenReturn(userGroups);

        // Act
        ResponseEntity<List<UserGroupDTO>> response = dictionaryController.getAllUserGroups();

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).hasSize(2);
        assertThat(response.getBody()).contains(userGroupDTO1, userGroupDTO2);
        verify(userGroupService).getAllUserGroups();
    }

    @Test
    @DisplayName("Powinien zwrócić wszystkie jednostki organizacyjne")
    void getAllOrganizationUnits_shouldReturnAllOrganizationUnits() {
        // Arrange
        when(organizationUnitService.getAllOrganizationUnits()).thenReturn(organizationUnits);

        // Act
        ResponseEntity<List<OrganizationUnitDTO>> response = dictionaryController.getAllOrganizationUnits();

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).hasSize(2);
        assertThat(response.getBody()).contains(orgUnitDTO1, orgUnitDTO2);
        verify(organizationUnitService).getAllOrganizationUnits();
    }
}
