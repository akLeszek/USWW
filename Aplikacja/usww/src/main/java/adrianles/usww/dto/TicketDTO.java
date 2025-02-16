package adrianles.usww.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TicketDTO {
    private Integer id;
    private String title;
    private String insertedDate;
    private String changeDate;
    private boolean archive;
    private Integer operatorId;
    private Integer studentId;
    private Integer statusId;
    private Integer categoryId;
}
