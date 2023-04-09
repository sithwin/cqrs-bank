package com.techbank.account.query.api.controllers;

import com.techbank.account.query.api.dto.AccountLookupResponse;
import com.techbank.account.query.api.dto.EqualityType;
import com.techbank.account.query.domain.BankAccount;
import com.techbank.account.query.queries.FindAccountByHolderQuery;
import com.techbank.account.query.queries.FindAccountByIdQuery;
import com.techbank.account.query.queries.FindAccountsWithBalanceQuery;
import com.techbank.account.query.queries.FindAllAccountsQuery;
import com.techbank.cqrs.core.infrastructure.QueryDispatcher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.MessageFormat;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/accountLookup")
public class AccountLookupController {
  private QueryDispatcher queryDispatcher;

  public AccountLookupController(QueryDispatcher queryDispatcher) {
    this.queryDispatcher = queryDispatcher;
  }

  @GetMapping(path = "/")
  public ResponseEntity<AccountLookupResponse> getAllAccounts() {
    log.info("Received request to get all accounts");

    try {
      List<BankAccount> accountList = queryDispatcher.send(new FindAllAccountsQuery());
      if (accountList.isEmpty()) {
        return new ResponseEntity<>(new AccountLookupResponse("No accounts found"), HttpStatus.NO_CONTENT);
      }

      var response = AccountLookupResponse.builder()
          .accounts(accountList)
          .message(MessageFormat.format("Successfully return {0} accounts", accountList.size()))
          .build();

      return new ResponseEntity<>(response, HttpStatus.OK);
    } catch (Exception e) {
      var safeErrorMessage = "Error while processing request to get all accounts";
      log.error(safeErrorMessage, e);
      return new ResponseEntity<>(new AccountLookupResponse(safeErrorMessage), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @GetMapping(path = "/byId/{id}")
  public ResponseEntity<AccountLookupResponse> getAccountById(@PathVariable(value = "id") String id) {
    log.info("Received request to get account by id: {}", id);

    try {
      List<BankAccount> accountList = queryDispatcher.send(new FindAccountByIdQuery(id));
      if(accountList.isEmpty()) {
        return new ResponseEntity<>(new AccountLookupResponse("No accounts found"), HttpStatus.NO_CONTENT);
      }

      var response = AccountLookupResponse.builder()
          .accounts(accountList)
          .message("Successfully return account")
          .build();

      return new ResponseEntity<>(response, HttpStatus.OK);
    } catch (Exception e) {
      var safeErrorMessage = "Error while processing request to get account by id";
      log.error(safeErrorMessage, e);
      return new ResponseEntity<>(new AccountLookupResponse(safeErrorMessage), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @GetMapping(path = "/byHolder/{accountHolder}")
  public ResponseEntity<AccountLookupResponse> getAccountByHolder(@PathVariable(value = "accountHolder") String accountHolder) {
    log.info("Received request to get account by holder: {}", accountHolder);

    try {
      List<BankAccount> accountList = queryDispatcher.send(new FindAccountByHolderQuery(accountHolder));
      if(accountList.isEmpty()) {
        return new ResponseEntity<>(new AccountLookupResponse("No accounts found"), HttpStatus.NO_CONTENT);
      }

      var response = AccountLookupResponse.builder()
          .accounts(accountList)
          .message("Successfully return account")
          .build();

      return new ResponseEntity<>(response, HttpStatus.OK);
    } catch (Exception e) {
      var safeErrorMessage = "Error while processing request to get account by holder";
      log.error(safeErrorMessage, e);
      return new ResponseEntity<>(new AccountLookupResponse(safeErrorMessage), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @GetMapping(path = "/withBalance/{equalityType}/{balance}")
  public ResponseEntity<AccountLookupResponse> getAccountWithBalance(
      @PathVariable(value = "equalityType") EqualityType equalityType,
      @PathVariable(value = "balance") double balance) {
    log.info("Received request to get account with balance: {} {}", equalityType, balance);

    try {
      List<BankAccount> accountList = queryDispatcher.send(new FindAccountsWithBalanceQuery(equalityType, balance));
      if(accountList.isEmpty()) {
        return new ResponseEntity<>(new AccountLookupResponse("No accounts found"), HttpStatus.NO_CONTENT);
      }

      var response = AccountLookupResponse.builder()
          .accounts(accountList)
          .message(MessageFormat.format("Successfully return {0} accounts", accountList.size()))
          .build();

      return new ResponseEntity<>(response, HttpStatus.OK);
    } catch (Exception e) {
      var safeErrorMessage = "Error while processing request to get account with balance";
      log.error(safeErrorMessage, e);
      return new ResponseEntity<>(new AccountLookupResponse(safeErrorMessage), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }
}
