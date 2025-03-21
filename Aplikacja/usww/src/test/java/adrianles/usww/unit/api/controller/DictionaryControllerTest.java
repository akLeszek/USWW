package adrianles.usww.unit.api.controller;

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
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class DictionaryControllerTest {

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

    private List<TicketCategoryDTO> categories;
    private List<TicketStatusDTO> statuses;
    private List<TicketPriorityDTO> priorities;
    private List<UserGroupDTO> userGroups;
    private List<OrganizationUnitDTO> organizationUnits;

    @BeforeEach
    void setUp() {
        categories = Arrays.asList(
                new TicketCategoryDTO(1, "LOGOWANIE", "Problemy z logowaniem"),
                new TicketCategoryDTO(2, "KURS", "Problemy z kursami")
        );

        statuses = Arrays.asList(
                new TicketStatusDTO(1, "NEW", "Nowe"),
                new TicketStatusDTO(2, "IN_PROGRESS", "W trakcie")
        );

        priorities = Arrays.asList(
                new TicketPriorityDTO(1, "LOW", "Niski"),
                new TicketPriorityDTO(2, "MEDIUM", "Średni")
        );

        userGroups = Arrays.asList(
                new UserGroupDTO(1, "ADMIN", "Administratorzy"),
                new UserGroupDTO(2, "STUDENT", "Studenci")
        );

        organizationUnits = Arrays.asList(
                new OrganizationUnitDTO(1, "WH", "Wydział Humanistyczny"),
                new OrganizationUnitDTO(2, "WNP", "Wydział Nauk Przyrodniczych")
        );
    }

    @Test
    @DisplayName("Powinien zwrócić wszystkie kategorie zgłoszeń")
    void shouldGetAllCategories() {
        when(ticketCategoryService.getAllTicketCategories()).thenReturn(categories);

        ResponseEntity<List<TicketCategoryDTO>> response = dictionaryController.getAllCategories();

        assertThat(response.getBody()).isEqualTo(categories);
        verify(ticketCategoryService).getAllTicketCategories();
    }

    @Test
    @DisplayName("Powinien zwrócić kategorię zgłoszenia po ID")
    void shouldGetCategoryById() {
        TicketCategoryDTO category = categories.get(0);
        when(ticketCategoryService.getTicketCategoryById(1)).thenReturn(category);

        ResponseEntity<TicketCategoryDTO> response = dictionaryController.getCategoryById(1);

        assertThat(response.getBody()).isEqualTo(category);
        verify(ticketCategoryService).getTicketCategoryById(1);
    }

    @Test
    @DisplayName("Powinien zwrócić kategorię zgłoszenia po identyfikatorze")
    void shouldGetCategoryByIdn() {
        TicketCategoryDTO category = categories.get(0);
        when(ticketCategoryService.getTicketCategoryByIdn("LOGOWANIE")).thenReturn(category);

        ResponseEntity<TicketCategoryDTO> response = dictionaryController.getCategoryByIdn("LOGOWANIE");

        assertThat(response.getBody()).isEqualTo(category);
        verify(ticketCategoryService).getTicketCategoryByIdn("LOGOWANIE");
    }

    @Test
    @DisplayName("Powinien zwrócić wszystkie statusy zgłoszeń")
    void shouldGetAllStatuses() {
        when(ticketStatusService.getAllTicketStatuses()).thenReturn(statuses);

        ResponseEntity<List<TicketStatusDTO>> response = dictionaryController.getAllStatuses();

        assertThat(response.getBody()).isEqualTo(statuses);
        verify(ticketStatusService).getAllTicketStatuses();
    }

    @Test
    @DisplayName("Powinien zwrócić status zgłoszenia po ID")
    void shouldGetTicketStatusById() {
        TicketStatusDTO status = statuses.get(0);
        when(ticketStatusService.getTicketStatusById(1)).thenReturn(status);

        ResponseEntity<TicketStatusDTO> response = dictionaryController.getTicketStatusById(1);

        assertThat(response.getBody()).isEqualTo(status);
        verify(ticketStatusService).getTicketStatusById(1);
    }

    @Test
    @DisplayName("Powinien zwrócić status zgłoszenia po identyfikatorze")
    void shouldGetTicketStatusByIdn() {
        TicketStatusDTO status = statuses.get(0);
        when(ticketStatusService.getTicketStatusByIdn("NEW")).thenReturn(status);

        ResponseEntity<TicketStatusDTO> response = dictionaryController.getTicketStatusByIdn("NEW");

        assertThat(response.getBody()).isEqualTo(status);
        verify(ticketStatusService).getTicketStatusByIdn("NEW");
    }

    @Test
    @DisplayName("Powinien zwrócić wszystkie priorytety zgłoszeń")
    void shouldGetAllPriorities() {
        when(ticketPriorityService.getAllTicketPriorities()).thenReturn(priorities);

        ResponseEntity<List<TicketPriorityDTO>> response = dictionaryController.getAllPriorities();

        assertThat(response.getBody()).isEqualTo(priorities);
        verify(ticketPriorityService).getAllTicketPriorities();
    }

    @Test
    @DisplayName("Powinien zwrócić priorytet zgłoszenia po ID")
    void shouldGetTicketPriorityById() {
        TicketPriorityDTO priority = priorities.get(0);
        when(ticketPriorityService.getTicketPriorityById(1)).thenReturn(priority);

        ResponseEntity<TicketPriorityDTO> response = dictionaryController.getTicketPriorityById(1);

        assertThat(response.getBody()).isEqualTo(priority);
        verify(ticketPriorityService).getTicketPriorityById(1);
    }

    @Test
    @DisplayName("Powinien zwrócić priorytet zgłoszenia po identyfikatorze")
    void shouldGetTicketPriorityByIdn() {
        TicketPriorityDTO priority = priorities.get(0);
        when(ticketPriorityService.getTicketPriorityByIdn("LOW")).thenReturn(priority);

        ResponseEntity<TicketPriorityDTO> response = dictionaryController.getTicketPriorityByIdn("LOW");

        assertThat(response.getBody()).isEqualTo(priority);
        verify(ticketPriorityService).getTicketPriorityByIdn("LOW");
    }

    @Test
    @DisplayName("Powinien zwrócić wszystkie grupy użytkowników")
    void shouldGetAllUserGroups() {
        when(userGroupService.getAllUserGroups()).thenReturn(userGroups);

        ResponseEntity<List<UserGroupDTO>> response = dictionaryController.getAllUserGroups();

        assertThat(response.getBody()).isEqualTo(userGroups);
        verify(userGroupService).getAllUserGroups();
    }

    @Test
    @DisplayName("Powinien zwrócić wszystkie jednostki organizacyjne")
    void shouldGetAllOrganizationUnits() {
        when(organizationUnitService.getAllOrganizationUnits()).thenReturn(organizationUnits);

        ResponseEntity<List<OrganizationUnitDTO>> response = dictionaryController.getAllOrganizationUnits();

        assertThat(response.getBody()).isEqualTo(organizationUnits);
        verify(organizationUnitService).getAllOrganizationUnits();
    }
}
