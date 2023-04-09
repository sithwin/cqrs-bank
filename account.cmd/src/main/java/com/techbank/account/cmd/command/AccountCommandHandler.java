package com.techbank.account.cmd.command;

import com.techbank.account.cmd.domain.AccountAggregate;
import com.techbank.cqrs.core.handlers.EventSourcingHandler;
import org.springframework.stereotype.Service;

@Service
public class AccountCommandHandler implements CommandHandler {
  private EventSourcingHandler<AccountAggregate> eventSourcingHandler;

  public AccountCommandHandler(EventSourcingHandler<AccountAggregate> eventSourcingHandler) {
    this.eventSourcingHandler = eventSourcingHandler;
  }

  @Override
  public void handle(OpenAccountCommand command) {
    var aggregate = new AccountAggregate(command);
    eventSourcingHandler.save(aggregate);
  }

  @Override
  public void handle(DepositFundsCommand command) {
    var aggregate = eventSourcingHandler.getById(command.getId());
    aggregate.depositFounds(command.getAmount());
    eventSourcingHandler.save(aggregate);
  }

  @Override
  public void handle(WithdrawFundsCommand command) {
    var aggregate = eventSourcingHandler.getById(command.getId());
    if (aggregate.getBalance() < command.getAmount()) {
      throw new IllegalStateException("Withdraw declined, insufficient funds!");
    }
    aggregate.withdrawFunds(command.getAmount());
    eventSourcingHandler.save(aggregate);
  }

  @Override
  public void handle(CloseAccountCommand command) {
    var aggregate = eventSourcingHandler.getById(command.getId());
    aggregate.closeAccount();
    eventSourcingHandler.save(aggregate);
  }

  @Override
  public void handle(RestoreReadDbCommand command) {
    eventSourcingHandler.republishEvents();
  }

  @Override
  public void handle(RestoreAccountCommand command) {
    eventSourcingHandler.republishByAggregate(command.getId());
  }
}
