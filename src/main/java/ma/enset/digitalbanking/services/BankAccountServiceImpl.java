package ma.enset.digitalbanking.services;

import lombok.RequiredArgsConstructor;
import ma.enset.digitalbanking.dtos.AccountHistoryDTO;
import ma.enset.digitalbanking.dtos.AccountOperationDTO;
import ma.enset.digitalbanking.dtos.BankAccountDTO;
import ma.enset.digitalbanking.dtos.CurrentBankAccountDTO;
import ma.enset.digitalbanking.dtos.CustomerDTO;
import ma.enset.digitalbanking.dtos.DashboardStatsDTO;
import ma.enset.digitalbanking.dtos.SavingBankAccountDTO;
import ma.enset.digitalbanking.entities.AccountOperation;
import ma.enset.digitalbanking.entities.BankAccount;
import ma.enset.digitalbanking.entities.CurrentAccount;
import ma.enset.digitalbanking.entities.Customer;
import ma.enset.digitalbanking.entities.SavingAccount;
import ma.enset.digitalbanking.enums.AccountStatus;
import ma.enset.digitalbanking.enums.OperationType;
import ma.enset.digitalbanking.exceptions.BalanceNotSufficientException;
import ma.enset.digitalbanking.exceptions.BankAccountNotFoundException;
import ma.enset.digitalbanking.exceptions.CustomerNotFoundException;
import ma.enset.digitalbanking.exceptions.OperationNotAllowedException;
import ma.enset.digitalbanking.mappers.BankAccountMapper;
import ma.enset.digitalbanking.repositories.AccountOperationRepository;
import ma.enset.digitalbanking.repositories.BankAccountRepository;
import ma.enset.digitalbanking.repositories.CustomerRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class BankAccountServiceImpl implements BankAccountService {

    private final CustomerRepository customerRepository;
    private final BankAccountRepository bankAccountRepository;
    private final AccountOperationRepository accountOperationRepository;
    private final BankAccountMapper bankAccountMapper;

    @Override
    public CustomerDTO saveCustomer(CustomerDTO customerDTO) {
        Customer customer = bankAccountMapper.fromCustomerDTO(customerDTO);
        customer.setCreatedAt(LocalDateTime.now());
        customer.setCreatedBy(currentUsername());
        Customer savedCustomer = customerRepository.save(customer);
        return bankAccountMapper.fromCustomer(savedCustomer);
    }

    @Override
    public List<CustomerDTO> listCustomers() {
        return customerRepository.findAll()
                .stream()
                .map(bankAccountMapper::fromCustomer)
                .toList();
    }

    @Override
    public CustomerDTO getCustomer(Long customerId) {
        Customer customer = findCustomer(customerId);
        return bankAccountMapper.fromCustomer(customer);
    }

    @Override
    public CustomerDTO updateCustomer(Long customerId, CustomerDTO customerDTO) {
        Customer customer = findCustomer(customerId);
        customer.setName(customerDTO.getName());
        customer.setEmail(customerDTO.getEmail());
        customer.setUpdatedAt(LocalDateTime.now());
        customer.setUpdatedBy(currentUsername());
        Customer updatedCustomer = customerRepository.save(customer);
        return bankAccountMapper.fromCustomer(updatedCustomer);
    }

    @Override
    public void deleteCustomer(Long customerId) {
        Customer customer = findCustomer(customerId);
        customerRepository.delete(customer);
    }

    @Override
    public List<CustomerDTO> searchCustomers(String keyword) {
        return customerRepository.findByNameContainsIgnoreCase(keyword == null ? "" : keyword)
                .stream()
                .map(bankAccountMapper::fromCustomer)
                .toList();
    }

    @Override
    public CurrentBankAccountDTO saveCurrentBankAccount(double initialBalance, double overDraft, Long customerId) {
        validateInitialBalance(initialBalance);
        Customer customer = findCustomer(customerId);
        CurrentAccount currentAccount = CurrentAccount.builder()
                .id(UUID.randomUUID().toString())
                .balance(initialBalance)
                .overDraft(overDraft)
                .createdAt(LocalDateTime.now())
                .status(AccountStatus.CREATED)
                .customer(customer)
                .createdBy(currentUsername())
                .build();
        CurrentAccount savedAccount = bankAccountRepository.save(currentAccount);
        return bankAccountMapper.fromCurrentBankAccount(savedAccount);
    }

    @Override
    public SavingBankAccountDTO saveSavingBankAccount(double initialBalance, double interestRate, Long customerId) {
        validateInitialBalance(initialBalance);
        Customer customer = findCustomer(customerId);
        SavingAccount savingAccount = SavingAccount.builder()
                .id(UUID.randomUUID().toString())
                .balance(initialBalance)
                .interestRate(interestRate)
                .createdAt(LocalDateTime.now())
                .status(AccountStatus.CREATED)
                .customer(customer)
                .createdBy(currentUsername())
                .build();
        SavingAccount savedAccount = bankAccountRepository.save(savingAccount);
        return bankAccountMapper.fromSavingBankAccount(savedAccount);
    }

    @Override
    @Transactional(readOnly = true)
    public BankAccountDTO getBankAccount(String accountId) {
        return bankAccountMapper.fromBankAccount(findBankAccount(accountId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<BankAccountDTO> bankAccountList() {
        return bankAccountRepository.findAll()
                .stream()
                .map(bankAccountMapper::fromBankAccount)
                .toList();
    }

    @Override
    public void debit(String accountId, double amount, String description) {
        validatePositiveAmount(amount);
        BankAccount bankAccount = findBankAccount(accountId);
        double availableBalance = availableBalance(bankAccount);
        if (availableBalance < amount) {
            throw new BalanceNotSufficientException("Balance not sufficient for account " + accountId);
        }
        AccountOperation accountOperation = buildOperation(bankAccount, amount, OperationType.DEBIT, description);
        accountOperationRepository.save(accountOperation);
        bankAccount.setBalance(bankAccount.getBalance() - amount);
        bankAccount.setUpdatedAt(LocalDateTime.now());
        bankAccount.setUpdatedBy(currentUsername());
        bankAccountRepository.save(bankAccount);
    }

    @Override
    public void credit(String accountId, double amount, String description) {
        validatePositiveAmount(amount);
        BankAccount bankAccount = findBankAccount(accountId);
        AccountOperation accountOperation = buildOperation(bankAccount, amount, OperationType.CREDIT, description);
        accountOperationRepository.save(accountOperation);
        bankAccount.setBalance(bankAccount.getBalance() + amount);
        bankAccount.setUpdatedAt(LocalDateTime.now());
        bankAccount.setUpdatedBy(currentUsername());
        bankAccountRepository.save(bankAccount);
    }

    @Override
    public void transfer(String accountSource, String accountDestination, double amount, String description) {
        if (accountSource.equals(accountDestination)) {
            throw new OperationNotAllowedException("Source and destination accounts must be different");
        }
        debit(accountSource, amount, description == null ? "Transfer debit" : description);
        credit(accountDestination, amount, description == null ? "Transfer credit" : description);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AccountOperationDTO> accountHistory(String accountId) {
        findBankAccount(accountId);
        return accountOperationRepository.findByBankAccountId(accountId)
                .stream()
                .map(bankAccountMapper::fromAccountOperation)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public AccountHistoryDTO getAccountHistory(String accountId, int page, int size) {
        BankAccount bankAccount = findBankAccount(accountId);
        Page<AccountOperation> accountOperationPage = accountOperationRepository.findByBankAccountId(
                accountId,
                PageRequest.of(page, size)
        );
        List<AccountOperationDTO> operationDTOS = accountOperationPage.getContent()
                .stream()
                .map(bankAccountMapper::fromAccountOperation)
                .toList();
        return AccountHistoryDTO.builder()
                .accountId(bankAccount.getId())
                .balance(bankAccount.getBalance())
                .currentPage(page)
                .pageSize(size)
                .totalPages(accountOperationPage.getTotalPages())
                .accountOperationDTOS(operationDTOS)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public DashboardStatsDTO dashboardStats() {
        List<BankAccount> accounts = bankAccountRepository.findAll();
        List<AccountOperation> operations = accountOperationRepository.findAll();

        Map<String, Long> operationsByType = operations.stream()
                .collect(Collectors.groupingBy(operation -> operation.getType().name(), Collectors.counting()));
        Map<String, Long> accountsByStatus = accounts.stream()
                .collect(Collectors.groupingBy(account -> account.getStatus().name(), Collectors.counting()));
        Map<String, Long> accountsByType = accounts.stream()
                .collect(Collectors.groupingBy(this::accountType, Collectors.counting()));

        return DashboardStatsDTO.builder()
                .totalCustomers(customerRepository.count())
                .totalAccounts(accounts.size())
                .totalBalance(accounts.stream().mapToDouble(BankAccount::getBalance).sum())
                .operationsByType(operationsByType)
                .accountsByStatus(accountsByStatus)
                .accountsByType(accountsByType)
                .build();
    }

    private Customer findCustomer(Long customerId) {
        return customerRepository.findById(customerId)
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found with id " + customerId));
    }

    private BankAccount findBankAccount(String accountId) {
        return bankAccountRepository.findById(accountId)
                .orElseThrow(() -> new BankAccountNotFoundException("Bank account not found with id " + accountId));
    }

    private AccountOperation buildOperation(
            BankAccount bankAccount,
            double amount,
            OperationType operationType,
            String description
    ) {
        return AccountOperation.builder()
                .operationDate(LocalDateTime.now())
                .amount(amount)
                .type(operationType)
                .bankAccount(bankAccount)
                .description(description)
                .createdBy(currentUsername())
                .build();
    }

    private void validatePositiveAmount(double amount) {
        if (amount <= 0) {
            throw new OperationNotAllowedException("Amount must be positive");
        }
    }

    private void validateInitialBalance(double initialBalance) {
        if (initialBalance < 0) {
            throw new OperationNotAllowedException("Initial balance must be greater than or equal to zero");
        }
    }

    private double availableBalance(BankAccount bankAccount) {
        if (bankAccount instanceof CurrentAccount currentAccount) {
            return currentAccount.getBalance() + currentAccount.getOverDraft();
        }
        return bankAccount.getBalance();
    }

    private String accountType(BankAccount bankAccount) {
        if (bankAccount instanceof CurrentAccount) {
            return "CURRENT";
        }
        if (bankAccount instanceof SavingAccount) {
            return "SAVING";
        }
        return "UNKNOWN";
    }

    private String currentUsername() {
        return "system";
    }
}
