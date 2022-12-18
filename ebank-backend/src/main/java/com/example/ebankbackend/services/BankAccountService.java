package com.example.ebankbackend.services;

import com.example.ebankbackend.dtos.*;

import java.util.Collection;

public interface BankAccountService {
    CustomerDTO saveCustomer(CustomerDTO customerDTO);
    CurrentBankAccountDTO saveCurrentBankAccount(double initialBalance, double overDraft, Long customerId);
    SavingBankAccountDTO saveSavingBankAccount(double initialBalance, double interestRate, Long customerId);
    Collection<CustomerDTO> customers();
    BankAccountDTO getBankAccount(String accountId);
    void debit(String accountId, double amount, String description);
    void credit(String accountId, double amount, String description);
    void transfert(String accountIdSource, String accountIdDestination, double amount);
    Collection<BankAccountDTO> bankAccounts();
    public CustomerDTO getCustomer(Long customerId);
    CustomerDTO updateCustomer(CustomerDTO customerDTO);
    public void deleteCustomer(Long customerId);
    Collection<AccountOperationDTO> accountHistory(String accountId);
    AccountHistoryDTO accountHistories(String accountId, int page, int size);
    Collection<CustomerDTO> searchCustomers(String keyword);
}
