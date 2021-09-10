package com.ddd.architecture.eventsourcing.model;

import org.jmolecules.ddd.types.AggregateRoot;
import org.jmolecules.ddd.types.Identifier;

public interface EventSourcedAggregateRoot<T extends EventSourcedAggregateRoot<T, ID>, ID extends Identifier>  extends Versioned, AggregateRoot<T, ID>{
}
