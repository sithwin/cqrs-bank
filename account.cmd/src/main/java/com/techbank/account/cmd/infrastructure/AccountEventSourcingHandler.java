package com.techbank.account.cmd.infrastructure;

import com.techbank.account.cmd.domain.AccountAggregate;
import com.techbank.cqrs.core.domain.AggregateRoot;
import com.techbank.cqrs.core.handlers.EventSourcingHandler;
import com.techbank.cqrs.core.infrastructure.EventProducer;
import com.techbank.cqrs.core.infrastructure.EventStore;
import org.springframework.stereotype.Service;

import java.util.Comparator;

@Service
public class AccountEventSourcingHandler implements EventSourcingHandler<AccountAggregate> {
  private EventStore eventStore;
  private EventProducer eventProducer;

  public AccountEventSourcingHandler(EventStore eventStore, EventProducer eventProducer) {
    this.eventStore = eventStore;
    this.eventProducer = eventProducer;
  }

  @Override
  public void save(AggregateRoot aggregate) {
    eventStore.saveEvents(aggregate.getId(), aggregate.getUncommittedChanges(), aggregate.getVersion());
    aggregate.markChangesAsCommitted();
  }

  @Override
  public AccountAggregate getById(String id) {
    var aggregate = new AccountAggregate();
    var events = eventStore.getEvents(id);

    if(events != null && !events.isEmpty()) {
      aggregate.replayEvent(events);
      var latestVersion = events.stream().map(x -> x.getVersion()).max(Comparator.naturalOrder());
      aggregate.setVersion(latestVersion.get());
    }
    return aggregate;
  }

  @Override
  public void republishEvents() {
    var aggregateIds = eventStore.getAggregateIds();
    for(var id: aggregateIds) {
      var aggregate = getById(id);
      if(aggregate == null || !aggregate.getActive()) {
        continue;
      }
      republishByAggregate(id);
    }
  }

  @Override
  public void republishByAggregate(String id) {
    var events = eventStore.getEvents(id);
    events.sort(Comparator.comparingInt(x -> x.getVersion()));
    for(var event: events) {
      eventProducer.produce(event.getClass().getSimpleName(), event);
    }
  }
}
