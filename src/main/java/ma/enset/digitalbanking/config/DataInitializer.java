package ma.enset.digitalbanking.config;

import lombok.RequiredArgsConstructor;
import ma.enset.digitalbanking.dtos.CurrentBankAccountDTO;
import ma.enset.digitalbanking.dtos.CustomerDTO;
import ma.enset.digitalbanking.dtos.SavingBankAccountDTO;
import ma.enset.digitalbanking.services.BankAccountService;
import ma.enset.digitalbanking.services.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class DataInitializer {

    private final BankAccountService bankAccountService;
    private final UserService userService;

    @Bean
    public CommandLineRunner seedBankingData() {
        return args -> {
            userService.saveRoleIfMissing(UserService.ROLE_ADMIN);
            userService.saveRoleIfMissing(UserService.ROLE_USER);
            userService.createUserIfMissing(
                    "admin",
                    "admin@example.com",
                    "admin123",
                    List.of(UserService.ROLE_ADMIN, UserService.ROLE_USER)
            );
            userService.createUserIfMissing(
                    "user",
                    "user@example.com",
                    "user123",
                    List.of(UserService.ROLE_USER)
            );

            if (!bankAccountService.listCustomers().isEmpty()) {
                return;
            }

            CustomerDTO ahmed = bankAccountService.saveCustomer(CustomerDTO.builder()
                    .name("Ahmed El Mansouri")
                    .email("ahmed@example.com")
                    .build());
            CustomerDTO salma = bankAccountService.saveCustomer(CustomerDTO.builder()
                    .name("Salma Bennani")
                    .email("salma@example.com")
                    .build());
            CustomerDTO youssef = bankAccountService.saveCustomer(CustomerDTO.builder()
                    .name("Youssef Alaoui")
                    .email("youssef@example.com")
                    .build());

            CurrentBankAccountDTO ahmedCurrentAccount = bankAccountService.saveCurrentBankAccount(
                    15_000,
                    5_000,
                    ahmed.getId()
            );
            SavingBankAccountDTO salmaSavingAccount = bankAccountService.saveSavingBankAccount(
                    30_000,
                    3.5,
                    salma.getId()
            );
            CurrentBankAccountDTO youssefCurrentAccount = bankAccountService.saveCurrentBankAccount(
                    8_000,
                    2_500,
                    youssef.getId()
            );

            bankAccountService.credit(ahmedCurrentAccount.getId(), 2_000, "Initial salary transfer");
            bankAccountService.debit(ahmedCurrentAccount.getId(), 750, "Card payment");
            bankAccountService.credit(salmaSavingAccount.getId(), 1_500, "Savings deposit");
            bankAccountService.transfer(
                    ahmedCurrentAccount.getId(),
                    youssefCurrentAccount.getId(),
                    500,
                    "Family transfer"
            );
        };
    }
}
