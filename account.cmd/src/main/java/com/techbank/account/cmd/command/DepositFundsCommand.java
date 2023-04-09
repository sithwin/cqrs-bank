package com.techbank.account.cmd.command;

import com.techbank.cqrs.core.commands.BaseCommand;
import lombok.Data;

@Data
public class DepositFundsCommand extends BaseCommand {
  private double amount;
}
