package com.techbank.account.query.queries;

import com.techbank.account.query.api.dto.EqualityType;
import com.techbank.account.query.domain.AccountRepository;
import com.techbank.account.query.domain.BankAccount;
import com.techbank.cqrs.core.domain.BaseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AccountQueryHandler implements QueryHandler {
  private AccountRepository accountRepository;
  public AccountQueryHandler(AccountRepository accountRepository) {
    this.accountRepository = accountRepository;
  }

  @Override
  public List<BaseEntity> handle(FindAllAccountsQuery query) {
    Iterable<BankAccount> accounts = accountRepository.findAll();
    List<BaseEntity> baseEntityList = new ArrayList<>();
    accounts.forEach(account -> baseEntityList.add(account));
    return baseEntityList;
  }

  @Override
  public List<BaseEntity> handle(FindAccountByIdQuery query) {
    var bankAccount = accountRepository.findById(query.getId());
    if(bankAccount.isEmpty()) {
      return new ArrayList<>();
    }

    List<BaseEntity> baseEntityList = new ArrayList<>();
    baseEntityList.add(bankAccount.get());
    return baseEntityList;
  }

  @Override
  public List<BaseEntity> handle(FindAccountByHolderQuery query) {
    var bankAccount = accountRepository.findByAccountHolder(query.getAccountHolder());
    if(bankAccount.isEmpty()) {
      return new ArrayList<>();
    }

    List<BaseEntity> baseEntityList = new ArrayList<>();
    baseEntityList.add(bankAccount.get());
    return baseEntityList;
  }

  @Override
  public List<BaseEntity> handle(FindAccountsWithBalanceQuery query) {
    List<BaseEntity> baseEntityList = query.getEqualityType() == EqualityType.GREATER_THAN ?
            accountRepository.findByBalanceGreaterThan(query.getBalance()) :
            accountRepository.findByBalanceLessThan(query.getBalance());
    return baseEntityList;
  }
}
