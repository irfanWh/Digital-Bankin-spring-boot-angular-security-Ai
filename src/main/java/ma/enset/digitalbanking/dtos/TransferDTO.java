package ma.enset.digitalbanking.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransferDTO {

    private String accountSource;
    private String accountDestination;
    private double amount;
    private String description;
}
