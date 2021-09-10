package com.ddd.balance.domain.model.event;

import com.ddd.architecture.eventsourcing.model.DomainEvent;
import com.ddd.architecture.eventsourcing.model.Version;
import com.ddd.balance.domain.model.entity.Balance;
import java.math.BigDecimal;
import java.util.UUID;

public record BalanceCreated(UUID balanceId, UUID customerId, BigDecimal originalBalance, Version version) implements
    DomainEvent<Balance> {

   @Override
   public String getType() {
      return "balance.created";
   }

   @Override
   public Version getVersion() {
      return version;
   }
}
