package com.example.ebankbackend.web;

import com.example.ebankbackend.dtos.*;
import com.example.ebankbackend.services.BankAccountService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
@AllArgsConstructor
@Slf4j
@CrossOrigin("*")
public class BankAccountRestController {
    private BankAccountService bankAccountService;

    @GetMapping("/accounts/{accountId}")
    public BankAccountDTO getBankAccount(@PathVariable String accountId){
        return bankAccountService.getBankAccount(accountId);
    }

    @GetMapping("/accounts")
    public Collection<BankAccountDTO> bankAccounts(){
        return bankAccountService.bankAccounts();
    }

    @GetMapping("/accounts/{accountId}/operations")
    public Collection<AccountOperationDTO> getHistory(@PathVariable String accountId){
        return bankAccountService.accountHistory(accountId);
    }

    @GetMapping("/accounts/{accountId}/pageOperations")
    public AccountHistoryDTO getHistories(
            @PathVariable String accountId,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "5") int size){
        return bankAccountService.accountHistories(accountId, page, size);
    }

    @PostMapping("/accounts/debit")
    public DebitDTO debit(@RequestBody DebitDTO debitDTO){
        bankAccountService.debit(debitDTO.getAccountId(), debitDTO.getAmount(), debitDTO.getDescription());
        return debitDTO;
    }

    @PostMapping("/accounts/credit")
    public CreditDTO credit(@RequestBody CreditDTO creditDTO){
        bankAccountService.credit(creditDTO.getAccountId(), creditDTO.getAmount(), creditDTO.getDescription());
        return creditDTO;
    }

    @PostMapping("/accounts/transfert")
    public void transfert(@RequestBody TransfertRequestDTO transfertRequestDTO){
        bankAccountService.transfert(transfertRequestDTO.getAccountIdSource(), transfertRequestDTO.getAccountIdDestination(), transfertRequestDTO.getAmount());
    }
}
