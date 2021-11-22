package com.ddd.balance.application;

import com.ddd.architecture.eventsourcing.eventstore.EventStore;
import com.ddd.balance.domain.model.entity.Balance;
import com.ddd.balance.domain.model.entity.BalanceId;
import com.ddd.balance.infrastructure.persistence.InMemoryEventStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BalancePortFactory {

   @Bean
   public EventStore<Balance, BalanceId> balanceEventStore() {
     return new InMemoryEventStore<>();
   }
}
