package com.ddd.architecture.eventsourcing.eventstore;

import com.ddd.architecture.Port;
import com.ddd.architecture.eventsourcing.model.AggregateIdentifier;
import com.ddd.architecture.eventsourcing.model.DomainEvents;
import com.ddd.architecture.eventsourcing.model.EventSourcedAggregateRoot;
import java.util.Optional;

@Port
public interface EventStore<T extends EventSourcedAggregateRoot<T, ID>, ID extends AggregateIdentifier> {


   Optional<DomainEvents<T, ID>> loadEvents(ID aggregateId);

   void saveNewAggregate(ID aggregateId, DomainEvents<T, ID> events);

   void updateExistingAggregate(ID aggregateId, DomainEvents<T, ID> events)
       throws StaleAggregateVersionException;
}
