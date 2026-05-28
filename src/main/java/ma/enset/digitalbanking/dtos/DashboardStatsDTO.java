package ma.enset.digitalbanking.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DashboardStatsDTO {

    private long totalCustomers;
    private long totalAccounts;
    private double totalBalance;
    private Map<String, Long> operationsByType;
    private Map<String, Long> accountsByStatus;
    private Map<String, Long> accountsByType;
}
