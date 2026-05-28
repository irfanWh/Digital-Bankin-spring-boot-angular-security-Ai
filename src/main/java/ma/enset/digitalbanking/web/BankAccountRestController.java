package ma.enset.digitalbanking.web;

import lombok.RequiredArgsConstructor;
import ma.enset.digitalbanking.dtos.AccountHistoryDTO;
import ma.enset.digitalbanking.dtos.AccountOperationDTO;
import ma.enset.digitalbanking.dtos.BankAccountDTO;
import ma.enset.digitalbanking.dtos.CurrentBankAccountDTO;
import ma.enset.digitalbanking.dtos.SavingBankAccountDTO;
import ma.enset.digitalbanking.services.BankAccountService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
@CrossOrigin("*")
public class BankAccountRestController {

    private final BankAccountService bankAccountService;

    @GetMapping
    public List<BankAccountDTO> accounts() {
        return bankAccountService.bankAccountList();
    }

    @GetMapping("/{accountId}")
    public BankAccountDTO getBankAccount(@PathVariable String accountId) {
        return bankAccountService.getBankAccount(accountId);
    }

    @PostMapping("/current")
    @ResponseStatus(HttpStatus.CREATED)
    public CurrentBankAccountDTO saveCurrentAccount(@RequestBody CurrentAccountRequest request) {
        return bankAccountService.saveCurrentBankAccount(
                request.initialBalance(),
                request.overDraft(),
                request.customerId()
        );
    }

    @PostMapping("/saving")
    @ResponseStatus(HttpStatus.CREATED)
    public SavingBankAccountDTO saveSavingAccount(@RequestBody SavingAccountRequest request) {
        return bankAccountService.saveSavingBankAccount(
                request.initialBalance(),
                request.interestRate(),
                request.customerId()
        );
    }

    @GetMapping("/{accountId}/operations")
    public List<AccountOperationDTO> accountOperations(@PathVariable String accountId) {
        return bankAccountService.accountHistory(accountId);
    }

    @GetMapping("/{accountId}/pageOperations")
    public AccountHistoryDTO accountHistory(
            @PathVariable String accountId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size
    ) {
        return bankAccountService.getAccountHistory(accountId, page, size);
    }

    public record CurrentAccountRequest(double initialBalance, double overDraft, Long customerId) {
    }

    public record SavingAccountRequest(double initialBalance, double interestRate, Long customerId) {
    }
}
