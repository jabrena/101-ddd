package com.ddd.architecture.eventsourcing.model;

import java.util.UUID;

public class DomainBusinessException extends RuntimeException {
   public final UUID aggregateId;

   public DomainBusinessException(UUID aggregateId, String message) {
      super(message);
      this.aggregateId = aggregateId;
   }
}
