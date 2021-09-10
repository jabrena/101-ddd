package com.ddd.architecture.eventsourcing.model;

public interface DomainEvent<T> extends Versioned {

   String getType();
}
