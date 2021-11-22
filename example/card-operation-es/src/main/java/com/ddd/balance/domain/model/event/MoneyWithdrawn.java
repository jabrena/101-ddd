package com.ddd.balance.domain.model.event;

import com.ddd.architecture.eventsourcing.model.DomainEvent;
import com.ddd.architecture.eventsourcing.model.Version;
import com.ddd.balance.domain.model.entity.Balance;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record MoneyWithdrawn(UUID balanceId,
                             BigDecimal amount, LocalDateTime withdrawalTime, Version version) implements DomainEvent<Balance> {

   public String getType() {
      return "balance.money-withdrawn";
   }

   @Override
   public Version getVersion() {
      return version;
   }
}