package com.ddd.architecture.eventsourcing.eventstore;


import com.ddd.architecture.eventsourcing.model.AggregateIdentifier;
import com.ddd.architecture.eventsourcing.model.DomainEvent;
import com.ddd.architecture.eventsourcing.model.EventSourcedAggregateRoot;

public interface EventStream<T extends EventSourcedAggregateRoot<T, ID>, ID extends AggregateIdentifier> extends
    Iterable<DomainEvent<T>> {

   ID getAggregateId();

   String getAggregateName();
}
