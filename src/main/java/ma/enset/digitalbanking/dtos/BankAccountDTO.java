package ma.enset.digitalbanking.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import ma.enset.digitalbanking.enums.AccountStatus;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class BankAccountDTO {

    private String id;
    private double balance;
    private LocalDateTime createdAt;
    private AccountStatus status;
    private CustomerDTO customerDTO;
    private String type;
    private String createdBy;
    private String updatedBy;
    private LocalDateTime updatedAt;
}
