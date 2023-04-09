package com.techbank.account.cmd.command;

import com.techbank.cqrs.core.commands.BaseCommand;

public class RestoreAccountCommand extends BaseCommand {
  public RestoreAccountCommand(String id) {
    super((id));
  }
}
