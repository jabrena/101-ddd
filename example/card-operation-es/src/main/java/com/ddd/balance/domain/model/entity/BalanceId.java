package com.ddd.balance.domain.model.entity;

import com.ddd.architecture.eventsourcing.model.AggregateIdentifier;
import java.util.UUID;

public record BalanceId(UUID id) implements AggregateIdentifier {

   public static final String AGGREGATE_ROOT_NAME = "card-operation.Balance";

   @Override
   public String getAggregateName() {
      return AGGREGATE_ROOT_NAME;
   }
}
