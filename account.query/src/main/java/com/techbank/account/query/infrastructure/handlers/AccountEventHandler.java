package com.techbank.account.query.infrastructure.handlers;

import com.techbank.account.common.events.AccountClosedEvent;
import com.techbank.account.common.events.AccountOpenedEvent;
import com.techbank.account.common.events.FundsDepositedEvent;
import com.techbank.account.common.events.FundsWithdrawnEvent;
import com.techbank.account.query.domain.AccountRepository;
import com.techbank.account.query.domain.BankAccount;
import com.techbank.account.query.infrastructure.clients.bankaccount.BankAccountClient;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class AccountEventHandler implements EventHandler {
  private AccountRepository accountRepository;
  private BankAccountClient bankAccountClient;

  public AccountEventHandler(AccountRepository accountRepository, BankAccountClient bankAccountClient) {
    this.accountRepository = accountRepository;
    this.bankAccountClient = bankAccountClient;
  }

  @Override
  public void on(AccountOpenedEvent event) {
    var bankAccount = BankAccount.builder()
        .id(event.getId())
        .accountType(event.getAccountType())
        .accountHolder(event.getAccountHolder())
        .balance(event.getOpeningBalance())
        .createdDate(event.getCreatedDate())
        .build();
    this.accountRepository.save(bankAccount);
  }

  @Override
  public void on(FundsDepositedEvent event) {
    var bankAccount = accountRepository.findById(event.getId());
    if(bankAccount.isEmpty()) {
      bankAccountClient.restoreAccount(event.getId());
      return;
    }
    var currentBalance = bankAccount.get().getBalance();
    var latestBalance = currentBalance + event.getAmount();
    bankAccount.get().setBalance(latestBalance);
    bankAccount.get().setUpdatedDate(new Date());
    accountRepository.save(bankAccount.get());
  }

  @Override
  public void on(FundsWithdrawnEvent event) {
    var bankAccount = accountRepository.findById(event.getId());
    if(bankAccount.isEmpty()) {
      bankAccountClient.restoreAccount(event.getId());
      return;
    }
    var currentBalance = bankAccount.get().getBalance();
    var latestBalance = currentBalance - event.getAmount();
    bankAccount.get().setBalance(latestBalance);
    bankAccount.get().setUpdatedDate(new Date());
    accountRepository.save(bankAccount.get());
  }

  @Override
  public void on(AccountClosedEvent event) {
    accountRepository.deleteById(event.getId());
  }
}
