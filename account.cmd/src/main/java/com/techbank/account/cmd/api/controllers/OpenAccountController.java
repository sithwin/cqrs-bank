package com.techbank.account.cmd.api.controllers;

import com.techbank.account.cmd.api.dto.OpenAccountResponse;
import com.techbank.account.cmd.command.OpenAccountCommand;
import com.techbank.account.common.dto.BaseResponse;
import com.techbank.cqrs.core.infrastructure.CommandDispatcher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.MessageFormat;
import java.util.UUID;
import java.util.logging.Level;

@Slf4j
@RestController
@RequestMapping(path = "/api/v1/openBankAccount")
public class OpenAccountController {

  private CommandDispatcher commandDispatcher;

  public OpenAccountController(CommandDispatcher commandDispatcher) {
    this.commandDispatcher = commandDispatcher;
  }

  @PostMapping
  public ResponseEntity<BaseResponse> openAccount(@RequestBody OpenAccountCommand command) {
    var id = UUID.randomUUID().toString();
    command.setId(id);

    try {
      commandDispatcher.send(command);
      return new ResponseEntity<>(new OpenAccountResponse("Bank account request completed successfully!", id), HttpStatus.CREATED);
    } catch (IllegalStateException e) {
      log.warn("Client made a bad request - {0}", e);
      return new ResponseEntity<>(new BaseResponse(e.toString()), HttpStatus.BAD_REQUEST);
    } catch (Exception e) {
      var safeErrorMessage = MessageFormat.format("Error while processing to open a new bank account for id - {0}", id);
      log.error(safeErrorMessage, e);
      return new ResponseEntity<>(new BaseResponse(e.toString()), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }
}
