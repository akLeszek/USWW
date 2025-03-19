package adrianles.usww.api.controller.common;

import adrianles.usww.api.dto.dictionary.*;
import adrianles.usww.service.facade.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/dictionaries")
@RequiredArgsConstructor
public class DictionaryController {
    private final TicketCategoryService ticketCategoryService;
    private final TicketStatusService ticketStatusService;
    private final TicketPriorityDictionaryService ticketPriorityDictionaryService;
    private final UserGroupService userGroupService;
    private final OrganizationUnitService organizationUnitService;

    @GetMapping("/categories")
    public ResponseEntity<List<TicketCategoryDTO>> getAllCategories() {
        return ResponseEntity.ok(ticketCategoryService.getAllTicketCategories());
    }

    @GetMapping("/categories/{id}")
    public ResponseEntity<TicketCategoryDTO> getCategoryById(@PathVariable Integer id) {
        return ResponseEntity.ok(ticketCategoryService.getTicketCategoryById(id));
    }

    @GetMapping("/categories/idn/{idn}")
    public ResponseEntity<TicketCategoryDTO> getCategoryByIdn(@PathVariable String idn) {
        return ResponseEntity.ok(ticketCategoryService.getTicketCategoryByIdn(idn));
    }

    @GetMapping("/statuses")
    public ResponseEntity<List<TicketStatusDTO>> getAllStatuses() {
        return ResponseEntity.ok(ticketStatusService.getAllTicketStatuses());
    }

    @GetMapping("/statuses/{id}")
    public ResponseEntity<TicketStatusDTO> getTicketStatusById(@PathVariable Integer id) {
        return ResponseEntity.ok(ticketStatusService.getTicketStatusById(id));
    }

    @GetMapping("/statuses/idn/{idn}")
    public ResponseEntity<TicketStatusDTO> getTicketStatusByIdn(@PathVariable String idn) {
        return ResponseEntity.ok(ticketStatusService.getTicketStatusByIdn(idn));
    }

    @GetMapping("/priorities")
    public ResponseEntity<List<TicketPriorityDTO>> getAllPriorities() {
        return ResponseEntity.ok(ticketPriorityDictionaryService.getAllTicketPriorities());
    }

    @GetMapping("/priorities/{id}")
    public ResponseEntity<TicketPriorityDTO> getTicketPriorityById(@PathVariable Integer id) {
        return ResponseEntity.ok(ticketPriorityDictionaryService.getTicketPriorityById(id));
    }

    @GetMapping("/priorities/idn/{idn}")
    public ResponseEntity<TicketPriorityDTO> getTicketPriorityByIdn(@PathVariable String idn) {
        return ResponseEntity.ok(ticketPriorityDictionaryService.getTicketPriorityByIdn(idn));
    }

    @GetMapping("/user-groups")
    public ResponseEntity<List<UserGroupDTO>> getAllUserGroups() {
        return ResponseEntity.ok(userGroupService.getAllUserGroups());
    }

    @GetMapping("/organization-units")
    public ResponseEntity<List<OrganizationUnitDTO>> getAllOrganizationUnits() {
        return ResponseEntity.ok(organizationUnitService.getAllOrganizationUnits());
    }
}
