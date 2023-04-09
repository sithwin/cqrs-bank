package com.techbank.account.cmd.api.controllers;

import com.techbank.account.cmd.command.CloseAccountCommand;
import com.techbank.account.cmd.command.RestoreAccountCommand;
import com.techbank.account.cmd.command.RestoreReadDbCommand;
import com.techbank.account.common.dto.BaseResponse;
import com.techbank.cqrs.core.infrastructure.CommandDispatcher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping(path = "/api/v1/restoreReadDb")
public class RestoreReadDbController {
  private CommandDispatcher commandDispatcher;

  public RestoreReadDbController(CommandDispatcher commandDispatcher) {
    this.commandDispatcher = commandDispatcher;
  }

  @RequestMapping(path = "/")
  @PostMapping
  public ResponseEntity<BaseResponse> restoreReadDb() {
    try {
      commandDispatcher.send(new RestoreReadDbCommand());
      return new ResponseEntity<>(new BaseResponse("Restore read database completed successfully!"), HttpStatus.CREATED);
    } catch (IllegalStateException e) {
      log.warn("Client made a bad request - {0}", e);
      return new ResponseEntity<>(new BaseResponse(e.toString()), HttpStatus.BAD_REQUEST);
    } catch (Exception e) {
      log.error("Error while restoring to read database", e);
      return new ResponseEntity<>(new BaseResponse(e.toString()), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @RequestMapping(path = "/{id}")
  @PostMapping
  public ResponseEntity<BaseResponse> restoreAccount(@PathVariable(value = "id") String id) {
    try {
      commandDispatcher.send(new RestoreAccountCommand(id));
      return new ResponseEntity<>(new BaseResponse("Restore account request completed successfully!"), HttpStatus.OK);
    } catch (IllegalStateException e) {
      log.warn("Client made a bad request - {}", e);
      return new ResponseEntity<>(new BaseResponse(e.toString()), HttpStatus.BAD_REQUEST);
    } catch (Exception e) {
      log.error("Error while processing to Restore account for id - {}", e);
      return new ResponseEntity<>(new BaseResponse(e.toString()), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }
}
