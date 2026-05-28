package ma.enset.digitalbanking.web;

import lombok.RequiredArgsConstructor;
import ma.enset.digitalbanking.dtos.CustomerDTO;
import ma.enset.digitalbanking.services.BankAccountService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
@CrossOrigin("*")
public class CustomerRestController {

    private final BankAccountService bankAccountService;

    @GetMapping
    public List<CustomerDTO> customers() {
        return bankAccountService.listCustomers();
    }

    @GetMapping("/{id}")
    public CustomerDTO getCustomer(@PathVariable Long id) {
        return bankAccountService.getCustomer(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CustomerDTO saveCustomer(@RequestBody CustomerDTO customerDTO) {
        return bankAccountService.saveCustomer(customerDTO);
    }

    @PutMapping("/{id}")
    public CustomerDTO updateCustomer(@PathVariable Long id, @RequestBody CustomerDTO customerDTO) {
        return bankAccountService.updateCustomer(id, customerDTO);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCustomer(@PathVariable Long id) {
        bankAccountService.deleteCustomer(id);
    }

    @GetMapping("/search")
    public List<CustomerDTO> searchCustomers(@RequestParam(defaultValue = "") String keyword) {
        return bankAccountService.searchCustomers(keyword);
    }
}
