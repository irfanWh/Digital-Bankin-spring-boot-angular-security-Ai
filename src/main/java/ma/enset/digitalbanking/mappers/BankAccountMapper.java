package ma.enset.digitalbanking.mappers;

import ma.enset.digitalbanking.dtos.AccountOperationDTO;
import ma.enset.digitalbanking.dtos.BankAccountDTO;
import ma.enset.digitalbanking.dtos.CurrentBankAccountDTO;
import ma.enset.digitalbanking.dtos.CustomerDTO;
import ma.enset.digitalbanking.dtos.SavingBankAccountDTO;
import ma.enset.digitalbanking.entities.AccountOperation;
import ma.enset.digitalbanking.entities.BankAccount;
import ma.enset.digitalbanking.entities.CurrentAccount;
import ma.enset.digitalbanking.entities.Customer;
import ma.enset.digitalbanking.entities.SavingAccount;
import org.springframework.stereotype.Service;

@Service
public class BankAccountMapper {

    public CustomerDTO fromCustomer(Customer customer) {
        if (customer == null) {
            return null;
        }
        return CustomerDTO.builder()
                .id(customer.getId())
                .name(customer.getName())
                .email(customer.getEmail())
                .createdBy(customer.getCreatedBy())
                .updatedBy(customer.getUpdatedBy())
                .createdAt(customer.getCreatedAt())
                .updatedAt(customer.getUpdatedAt())
                .build();
    }

    public Customer fromCustomerDTO(CustomerDTO customerDTO) {
        if (customerDTO == null) {
            return null;
        }
        return Customer.builder()
                .id(customerDTO.getId())
                .name(customerDTO.getName())
                .email(customerDTO.getEmail())
                .createdBy(customerDTO.getCreatedBy())
                .updatedBy(customerDTO.getUpdatedBy())
                .createdAt(customerDTO.getCreatedAt())
                .updatedAt(customerDTO.getUpdatedAt())
                .build();
    }

    public BankAccountDTO fromBankAccount(BankAccount bankAccount) {
        if (bankAccount instanceof CurrentAccount currentAccount) {
            return fromCurrentBankAccount(currentAccount);
        }
        if (bankAccount instanceof SavingAccount savingAccount) {
            return fromSavingBankAccount(savingAccount);
        }
        return null;
    }

    public CurrentBankAccountDTO fromCurrentBankAccount(CurrentAccount currentAccount) {
        if (currentAccount == null) {
            return null;
        }
        return CurrentBankAccountDTO.builder()
                .id(currentAccount.getId())
                .balance(currentAccount.getBalance())
                .createdAt(currentAccount.getCreatedAt())
                .status(currentAccount.getStatus())
                .customerDTO(fromCustomer(currentAccount.getCustomer()))
                .type("CURRENT")
                .overDraft(currentAccount.getOverDraft())
                .createdBy(currentAccount.getCreatedBy())
                .updatedBy(currentAccount.getUpdatedBy())
                .updatedAt(currentAccount.getUpdatedAt())
                .build();
    }

    public SavingBankAccountDTO fromSavingBankAccount(SavingAccount savingAccount) {
        if (savingAccount == null) {
            return null;
        }
        return SavingBankAccountDTO.builder()
                .id(savingAccount.getId())
                .balance(savingAccount.getBalance())
                .createdAt(savingAccount.getCreatedAt())
                .status(savingAccount.getStatus())
                .customerDTO(fromCustomer(savingAccount.getCustomer()))
                .type("SAVING")
                .interestRate(savingAccount.getInterestRate())
                .createdBy(savingAccount.getCreatedBy())
                .updatedBy(savingAccount.getUpdatedBy())
                .updatedAt(savingAccount.getUpdatedAt())
                .build();
    }

    public AccountOperationDTO fromAccountOperation(AccountOperation accountOperation) {
        if (accountOperation == null) {
            return null;
        }
        String bankAccountId = accountOperation.getBankAccount() == null
                ? null
                : accountOperation.getBankAccount().getId();

        return AccountOperationDTO.builder()
                .id(accountOperation.getId())
                .operationDate(accountOperation.getOperationDate())
                .amount(accountOperation.getAmount())
                .type(accountOperation.getType())
                .bankAccountId(bankAccountId)
                .description(accountOperation.getDescription())
                .createdBy(accountOperation.getCreatedBy())
                .build();
    }
}
