package adrianles.usww.api.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TicketFilterCriteriaDTO {
    private String title;
    private Integer studentId;
    private Integer operatorId;
    private Integer statusId;
    private Integer categoryId;
    private Integer priorityId;
    private Boolean archive;
    private String fromDate;
    private String toDate;
    private String lastActivityFrom;
    private String lastActivityTo;
    private String sortBy;
    private String sortOrder;
}
