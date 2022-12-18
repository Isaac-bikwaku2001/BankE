package com.example.ebankbackend.web;

import com.example.ebankbackend.dtos.CustomerDTO;
import com.example.ebankbackend.entities.Customer;
import com.example.ebankbackend.services.BankAccountService;
import com.example.ebankbackend.services.BankAccountServiceImpl;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
@AllArgsConstructor
@Slf4j
@CrossOrigin("*")
public class CustomerRestController {
    private BankAccountServiceImpl bankAccountService;

    @GetMapping("/customers")
    public Collection<CustomerDTO> customers(){
        return bankAccountService.customers();
    }

    @GetMapping("/customers/search")
    public Collection<CustomerDTO> searchCustomers(@RequestParam(name = "keyword", defaultValue = "") String keyword){
        return bankAccountService.searchCustomers(keyword);
    }

    @GetMapping("/customers/{id}")
    public CustomerDTO getCustomer(@PathVariable(name = "id") Long customerId){
        return bankAccountService.getCustomer(customerId);
    }

    @PostMapping("/customers")
    public CustomerDTO saveCustomer(@RequestBody CustomerDTO customerDTO){
        return bankAccountService.saveCustomer(customerDTO);
    }

    @PutMapping("/customers/{customerId}")
    public CustomerDTO updateCustomer(@PathVariable Long customerId, @RequestBody CustomerDTO customerDTO){
        customerDTO.setId(customerId);
        return bankAccountService.updateCustomer(customerDTO);
    }

    @DeleteMapping("/customers/{id}")
    public void deleteCustomer(@PathVariable Long id){
        bankAccountService.deleteCustomer(id);
    }
}
