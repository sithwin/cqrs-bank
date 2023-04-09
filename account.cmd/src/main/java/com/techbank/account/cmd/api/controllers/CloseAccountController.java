package com.techbank.account.cmd.api.controllers;

import com.techbank.account.cmd.command.CloseAccountCommand;
import com.techbank.account.cmd.command.WithdrawFundsCommand;
import com.techbank.account.common.dto.BaseResponse;
import com.techbank.cqrs.core.infrastructure.CommandDispatcher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping(path = "/api/v1/closeAccount")
public class CloseAccountController {
  private CommandDispatcher commandDispatcher;

  public CloseAccountController(CommandDispatcher commandDispatcher) {
    this.commandDispatcher = commandDispatcher;
  }

  @DeleteMapping(path = "/{id}")
  public ResponseEntity<BaseResponse> closeAccount(@PathVariable(value = "id") String id) {
    try {
      commandDispatcher.send(new CloseAccountCommand(id));
      return new ResponseEntity<>(new BaseResponse("Close account request completed successfully!"), HttpStatus.OK);
    } catch (IllegalStateException e) {
      log.warn("Client made a bad request - {}", e);
      return new ResponseEntity<>(new BaseResponse(e.toString()), HttpStatus.BAD_REQUEST);
    } catch (Exception e) {
      log.error("Error while processing to Close account for id - {}", e);
      return new ResponseEntity<>(new BaseResponse(e.toString()), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }
}
