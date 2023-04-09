package com.techbank.account.cmd.command;

import com.techbank.account.common.dto.AccountType;
import com.techbank.cqrs.core.commands.BaseCommand;
import lombok.Data;

@Data
public class OpenAccountCommand extends BaseCommand {
  private String accountHolder;
  private AccountType accountType;
  private double openingBalance;
}
