package com.techbank.account.query.infrastructure.handlers;

import com.techbank.account.common.events.AccountClosedEvent;
import com.techbank.account.common.events.AccountOpenedEvent;
import com.techbank.account.common.events.FundsDepositedEvent;
import com.techbank.account.common.events.FundsWithdrawnEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;

@Slf4j
@Service
public class AccountEventConsumer implements EventConsumer{
  private EventHandler eventHandler;

  public AccountEventConsumer(EventHandler eventHandler) {
    this.eventHandler = eventHandler;
  }

  @KafkaListener(topics = "AccountOpenedEvent", groupId = "${spring.kafka.consumer.group-id}")
  @Override
  public void consume(AccountOpenedEvent event, Acknowledgment ack) {
    log.info("Consume AccountOpenedEvent - {}", event.toString());
    eventHandler.on(event);
    ack.acknowledge();
  }

  @KafkaListener(topics = "FundsDepositedEvent", groupId = "${spring.kafka.consumer.group-id}")
  @Override
  public void consume(FundsDepositedEvent event, Acknowledgment ack) {
    log.info("Consume FundsDepositedEvent - {}", event.toString());
    try {
      eventHandler.on(event);
    } catch (Exception e) {
      var safeErrorMessage = MessageFormat.format("Error while consuming FundsDepositedEvent - {0}", e);
      log.error(safeErrorMessage, e);
    }

    ack.acknowledge();
  }

  @KafkaListener(topics = "FundsWithdrawnEvent", groupId = "${spring.kafka.consumer.group-id}")
  @Override
  public void consume(FundsWithdrawnEvent event, Acknowledgment ack) {
    log.info("Consume FundsWithdrawnEvent - {}", event.toString());
    try {
      eventHandler.on(event);
    } catch (Exception e) {
      var safeErrorMessage = MessageFormat.format("Error while consuming FundsWithdrawnEvent - {0}", e);
      log.error(safeErrorMessage, e);
    }
    ack.acknowledge();
  }

  @KafkaListener(topics = "AccountClosedEvent", groupId = "${spring.kafka.consumer.group-id}")
  @Override
  public void consume(AccountClosedEvent event, Acknowledgment ack) {
    log.info("Consume AccountClosedEvent - {}", event);
    try {
      eventHandler.on(event);
    } catch (Exception e) {
      var safeErrorMessage = MessageFormat.format("Error while consuming AccountClosedEvent - {0}", e);
      log.error(safeErrorMessage, e);
    }
    ack.acknowledge();
  }
}
