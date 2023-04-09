package com.techbank.account.query.infrastructure.clients.bankaccount;

import com.techbank.account.common.dto.BaseResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "bank-account", url = "http://localhost:5000/api/v1/restoreReadDb/")
public interface BankAccountClient {

  @PostMapping("/{id}")
  public ResponseEntity<BaseResponse> restoreAccount(@PathVariable(value = "id") String id);
}
