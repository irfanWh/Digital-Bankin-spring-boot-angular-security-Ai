package ma.enset.digitalbanking.web;

import lombok.RequiredArgsConstructor;
import ma.enset.digitalbanking.dtos.CreditDTO;
import ma.enset.digitalbanking.dtos.DebitDTO;
import ma.enset.digitalbanking.dtos.TransferDTO;
import ma.enset.digitalbanking.services.BankAccountService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
@CrossOrigin("*")
public class OperationRestController {

    private final BankAccountService bankAccountService;

    @PostMapping("/debit")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void debit(@RequestBody DebitDTO debitDTO) {
        bankAccountService.debit(
                debitDTO.getAccountId(),
                debitDTO.getAmount(),
                debitDTO.getDescription()
        );
    }

    @PostMapping("/credit")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void credit(@RequestBody CreditDTO creditDTO) {
        bankAccountService.credit(
                creditDTO.getAccountId(),
                creditDTO.getAmount(),
                creditDTO.getDescription()
        );
    }

    @PostMapping("/transfer")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void transfer(@RequestBody TransferDTO transferDTO) {
        bankAccountService.transfer(
                transferDTO.getAccountSource(),
                transferDTO.getAccountDestination(),
                transferDTO.getAmount(),
                transferDTO.getDescription()
        );
    }
}
