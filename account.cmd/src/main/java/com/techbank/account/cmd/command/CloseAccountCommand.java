package com.techbank.account.cmd.command;

import com.techbank.cqrs.core.commands.BaseCommand;

public class CloseAccountCommand extends BaseCommand {
  public CloseAccountCommand(String id) {
    super((id));
  }
}
