package ma.enset.digitalbanking.web;

import lombok.RequiredArgsConstructor;
import ma.enset.digitalbanking.dtos.DashboardStatsDTO;
import ma.enset.digitalbanking.services.BankAccountService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@CrossOrigin("*")
public class DashboardController {

    private final BankAccountService bankAccountService;

    @GetMapping("/stats")
    public DashboardStatsDTO stats() {
        return bankAccountService.dashboardStats();
    }
}
