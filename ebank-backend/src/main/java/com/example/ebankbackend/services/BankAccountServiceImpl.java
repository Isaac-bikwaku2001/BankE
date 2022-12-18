package com.example.ebankbackend.services;

import com.example.ebankbackend.dtos.*;
import com.example.ebankbackend.entities.*;
import com.example.ebankbackend.enums.OperationType;
import com.example.ebankbackend.exceptions.BalanceNotSufficientException;
import com.example.ebankbackend.exceptions.BankAccountNotFoundException;
import com.example.ebankbackend.exceptions.CustomerNotFoundException;
import com.example.ebankbackend.mappers.BankAccountMapperImpl;
import com.example.ebankbackend.repositories.AccountOperationRepository;
import com.example.ebankbackend.repositories.BankAccountRepository;
import com.example.ebankbackend.repositories.CustomerRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
@AllArgsConstructor
@Slf4j
public class BankAccountServiceImpl implements BankAccountService {
    private CustomerRepository customerRepository;
    private BankAccountRepository bankAccountRepository;
    private AccountOperationRepository accountOperationRepository;
    private BankAccountMapperImpl bankAccountMapper;

    private BankAccount bankAccount(String accountId) {
        BankAccount bankAccount = bankAccountRepository.findById(accountId)
                .orElseThrow(() -> new BankAccountNotFoundException("Bank account not found"));

        return bankAccount;
    }

    @Override
    public CustomerDTO saveCustomer(CustomerDTO customerDTO) {
        log.info("Saving a new customer");
        Customer customer = bankAccountMapper.fromCustomerDTO(customerDTO);
        Customer savedCustomer = customerRepository.save(customer);
        return bankAccountMapper.fromCustomer(savedCustomer);
    }

    @Override
    public CurrentBankAccountDTO saveCurrentBankAccount(double initialBalance, double overDraft, Long customerId) {
        log.info("Saving a current bank account");
        Customer customer = customerRepository.findById(customerId).orElse(null);
        CurrentAccount currentAccount = new CurrentAccount();

        if (customer == null)
            throw new CustomerNotFoundException("Customer not found");

        currentAccount.setId(UUID.randomUUID().toString());
        currentAccount.setCreateAt(new Date());
        currentAccount.setBalance(initialBalance);
        currentAccount.setOverDraft(overDraft);
        currentAccount.setCustomer(customer);
        CurrentAccount savedCurrentAccount = bankAccountRepository.save(currentAccount);

        return bankAccountMapper.fromCurrentBankAccount(savedCurrentAccount);
    }

    @Override
    public SavingBankAccountDTO saveSavingBankAccount(double initialBalance, double interestRate, Long customerId) {
        log.info("Saving a save bank account");
        Customer customer = customerRepository.findById(customerId).orElse(null);
        SavingAccount savingAccount = new SavingAccount();

        if (customer == null)
            throw new CustomerNotFoundException("Customer not found");

        savingAccount.setId(UUID.randomUUID().toString());
        savingAccount.setCreateAt(new Date());
        savingAccount.setBalance(initialBalance);
        savingAccount.setInterestRate(interestRate);
        savingAccount.setCustomer(customer);
        SavingAccount savedSavingAccount = bankAccountRepository.save(savingAccount);

        return bankAccountMapper.fromSavingBankAccount(savedSavingAccount);
    }

    @Override
    public Collection<CustomerDTO> customers() {
        log.info("get all customers");
        Collection<Customer> customers = customerRepository.findAll();
        Collection<CustomerDTO> customerDTOS = customers.stream().map(
                customer -> bankAccountMapper.fromCustomer(customer)
        ).collect(Collectors.toList());

        /*Collection<CustomerDTO> customerDTOS = new ArrayList<>();
        for (Customer customer : customers){
            CustomerDTO customerDTO = bankAccountMapper.fromCustomer(customer);
            customerDTOS.add(customerDTO);
        }*/

        return customerDTOS;
    }

    @Override
    public BankAccountDTO getBankAccount(String accountId) {
        log.info("get a bank account");
        BankAccount bankAccount = bankAccount(accountId);

        if (bankAccount instanceof SavingAccount) {
            SavingAccount savingAccount = (SavingAccount) bankAccount;
            return bankAccountMapper.fromSavingBankAccount(savingAccount);
        } else {
            CurrentAccount currentAccount = (CurrentAccount) bankAccount;
            return bankAccountMapper.fromCurrentBankAccount(currentAccount);
        }
    }

    @Override
    public void debit(String accountId, double amount, String description) {
        log.info("debit a bank account");
        BankAccount bankAccount = bankAccount(accountId);

        if (bankAccount.getBalance() < amount)
            throw new BalanceNotSufficientException("Balance not sufficient");

        AccountOperation accountOperation = new AccountOperation();
        accountOperation.setType(OperationType.DEBIT);
        accountOperation.setAmount(amount);
        accountOperation.setDescription(description);
        accountOperation.setOperationDate(new Date());
        accountOperation.setBankAccount(bankAccount);
        accountOperationRepository.save(accountOperation);
        bankAccount.setBalance(bankAccount.getBalance() - amount);
        bankAccountRepository.save(bankAccount);
    }

    @Override
    public void credit(String accountId, double amount, String description) {
        log.info("credit a bank account");
        BankAccount bankAccount = bankAccount(accountId);

        AccountOperation accountOperation = new AccountOperation();
        accountOperation.setType(OperationType.CREDIT);
        accountOperation.setAmount(amount);
        accountOperation.setDescription(description);
        accountOperation.setOperationDate(new Date());
        accountOperation.setBankAccount(bankAccount);
        accountOperationRepository.save(accountOperation);
        bankAccount.setBalance(bankAccount.getBalance() + amount);
        bankAccountRepository.save(bankAccount);
    }

    @Override
    public void transfert(String accountIdSource, String accountIdDestination, double amount) {
        log.info("Transfert money");
        debit(accountIdSource, amount, "Transfert to " + accountIdDestination);
        credit(accountIdDestination, amount, "Transfert to " + accountIdSource);
    }

    @Override
    public Collection<BankAccountDTO> bankAccounts() {
        Collection<BankAccount> bankAccounts = bankAccountRepository.findAll();
        Collection<BankAccountDTO> bankAccountDTOS = bankAccounts.stream().map(bankAccount -> {
            if (bankAccount instanceof SavingAccount) {
                SavingAccount savingAccount = (SavingAccount) bankAccount;
                return bankAccountMapper.fromSavingBankAccount(savingAccount);
            } else {
                CurrentAccount currentAccount = (CurrentAccount) bankAccount;
                return bankAccountMapper.fromCurrentBankAccount(currentAccount);
            }
        }).collect(Collectors.toList());

        return bankAccountDTOS;
    }

    @Override
    public CustomerDTO getCustomer(Long customerId) {
        return bankAccountMapper.fromCustomer(
                customerRepository.findById(customerId)
                        .orElseThrow(() -> new CustomerNotFoundException("Customer not found"))
        );
    }

    @Override
    public CustomerDTO updateCustomer(CustomerDTO customerDTO) {
        log.info("Saving a new customer");
        Customer customer = bankAccountMapper.fromCustomerDTO(customerDTO);
        Customer savedCustomer = customerRepository.save(customer);
        return bankAccountMapper.fromCustomer(savedCustomer);
    }

    @Override
    public void deleteCustomer(Long customerId) {
        customerRepository.deleteById(customerId);
    }

    @Override
    public Collection<AccountOperationDTO> accountHistory(String accountId) {
        Collection<AccountOperation> accountOperations = accountOperationRepository.findAccountOperationById(accountId);
        return accountOperations.stream().map(op -> bankAccountMapper.fromAccountOperation(op)
        ).collect(Collectors.toList());
    }

    @Override
    public AccountHistoryDTO accountHistories(String accountId, int page, int size) {
        BankAccount bankAccount = bankAccountRepository.findById(accountId).orElse(null);

        if (bankAccount == null) throw new BankAccountNotFoundException("Bank account not found");
        Page<AccountOperation> accountOperations = accountOperationRepository.findAccountOperationById(accountId, PageRequest.of(page, size));
        AccountHistoryDTO accountHistoryDTO = new AccountHistoryDTO();
        accountHistoryDTO.setAccountOperationDTOS(
                accountOperations.getContent().stream().map(
                        op -> bankAccountMapper.fromAccountOperation(op)).collect(Collectors.toList()
                )
        );
        accountHistoryDTO.setAccountId(bankAccount.getId());
        accountHistoryDTO.setBalance(bankAccount.getBalance());
        accountHistoryDTO.setCurrentPage(page);
        accountHistoryDTO.setPageSize(size);
        accountHistoryDTO.setTotalPages((int) accountOperations.getTotalElements());
        return accountHistoryDTO;
    }

    @Override
    public Collection<CustomerDTO> searchCustomers(String keyword) {
        Collection<Customer> customers = customerRepository.findCustomersByNomContains(keyword);
        Collection<CustomerDTO> customerDTOS = customers.stream().map(
                customer -> bankAccountMapper.fromCustomer(customer)
        ).collect(Collectors.toList());
        return customerDTOS;
    }
}
