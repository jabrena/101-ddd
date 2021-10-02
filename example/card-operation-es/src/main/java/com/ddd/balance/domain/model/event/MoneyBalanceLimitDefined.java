package com.ddd.balance.domain.model.event;

import com.ddd.architecture.eventsourcing.model.DomainEvent;
import com.ddd.architecture.eventsourcing.model.Version;
import com.ddd.balance.domain.model.entity.Balance;
import java.math.BigDecimal;
import java.util.UUID;

public record MoneyBalanceLimitDefined(UUID balanceId, BigDecimal balanceLimit,
                                       Version version) implements DomainEvent<Balance> {

   public String getType() {
      return "balance.money-balance-limit-defined";
   }

   @Override
   public Version getVersion() {
      return version;
   }
}
