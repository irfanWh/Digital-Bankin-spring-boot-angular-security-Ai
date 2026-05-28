package ma.enset.digitalbanking.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ma.enset.digitalbanking.enums.OperationType;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountOperationDTO {

    private Long id;
    private LocalDateTime operationDate;
    private double amount;
    private OperationType type;
    private String bankAccountId;
    private String description;
    private String createdBy;
}
