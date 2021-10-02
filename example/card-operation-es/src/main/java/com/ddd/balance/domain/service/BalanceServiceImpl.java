package com.ddd.balance.domain.service;

import com.ddd.architecture.eventsourcing.eventstore.EventStore;
import com.ddd.architecture.eventsourcing.model.DomainEvents;
import com.ddd.balance.domain.model.entity.Balance;
import com.ddd.balance.domain.model.entity.BalanceId;
import com.ddd.balance.domain.model.event.BalanceCreated;
import com.ddd.balance.domain.model.event.MoneyBalanceLimitAlreadyDefined;
import com.ddd.balance.domain.model.event.MoneyBalanceLimitDefined;
import com.ddd.balance.domain.model.event.MoneyRepaid;
import com.ddd.balance.domain.model.event.MoneyWithdrawalFailedDueToInsufficientBalance;
import com.ddd.balance.domain.model.event.MoneyWithdrawalFailedDueToLimitNotDefined;
import com.ddd.balance.domain.model.event.MoneyWithdrawalFailedDueToRecentWithdrawal;
import com.ddd.balance.domain.model.event.MoneyWithdrawn;
import com.ddd.balance.domain.service.FailedOperationResult.FailureReason;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class BalanceServiceImpl implements BalanceService {

   private final EventStore<Balance, BalanceId> balanceEventStore;

   public BalanceServiceImpl(EventStore<Balance, BalanceId> balanceEventStore) {
      this.balanceEventStore = balanceEventStore;
   }

   @Override
   public BalanceOperationResult createBalance(UUID customerId, BigDecimal amount) {
      BalanceId balanceId = new BalanceId(UUID.randomUUID());

      DomainEvents<Balance, BalanceId> domainEvents = Balance.createBalance(balanceId, customerId,
          amount);

      if (domainEvents.hasEventMatching(MoneyBalanceLimitAlreadyDefined.class)) {
         return new FailedOperationResult(FailureReason.BALANCE_LIMIT_ALREADY_DEFINED);
      } else if (domainEvents.hasEventMatching(BalanceCreated.class)) {
         //TODO: verify success of event store operation
         balanceEventStore.saveNewAggregate(balanceId, domainEvents);
         return BalanceOperationResult.success(balanceId.id());
      } else {
         return BalanceOperationResult.failure(FailureReason.UNKNOWN_FAILURE_REASON);
      }
   }


   @Override
   public Optional<Balance> getBalance(UUID balanceId) {
      return loadBalance(balanceId);
   }

   @Override
   public BalanceOperationResult defineLimit(UUID balanceId, BigDecimal balanceLimit) {
      Optional<Balance> currentBalance = loadBalance(balanceId);

      if (currentBalance.isEmpty()) {
         return BalanceOperationResult.failure(FailureReason.BALANCE_DOES_NOT_EXIST);
      }

      DomainEvents<Balance, BalanceId> domainEvents = currentBalance.get()
          .defineBalanceLimit(balanceLimit);

      if (domainEvents.hasEventMatching(MoneyBalanceLimitAlreadyDefined.class)) {
         return new FailedOperationResult(FailureReason.BALANCE_LIMIT_ALREADY_DEFINED);
      } else if (domainEvents.hasEventMatching(MoneyBalanceLimitDefined.class)) {
         //TODO: verify success of event store operation
         updateBalance(balanceId, domainEvents);
         return BalanceOperationResult.success(balanceId);
      } else {
         return BalanceOperationResult.failure(FailureReason.UNKNOWN_FAILURE_REASON);
      }
   }

   private Optional<Balance> loadBalance(UUID balanceUUID) {
      return balanceEventStore.loadEvents(new BalanceId(balanceUUID)).map(Balance::reHydrate);
   }

   private void updateBalance(UUID balanceUUID,
       DomainEvents<Balance, BalanceId> domainEvents) {
      BalanceId balanceId = new BalanceId(balanceUUID);
      balanceEventStore.updateExistingAggregate(balanceId, domainEvents);
   }

   @Override
   public BalanceOperationResult withdraw(UUID balanceId, BigDecimal amount) {
      Optional<Balance> currentBalance = loadBalance(balanceId);

      if (currentBalance.isEmpty()) {
         return BalanceOperationResult.failure(FailureReason.BALANCE_DOES_NOT_EXIST);
      }

      DomainEvents<Balance, BalanceId> domainEvents = currentBalance.get()
          .withdraw(amount, LocalDateTime.now());

      if (domainEvents.hasEventMatching(MoneyWithdrawalFailedDueToInsufficientBalance.class)) {
         return new FailedOperationResult(FailureReason.WITHDRAWAL_FAILED_DUE_TO_INSUFFICIENT_BALANCE);
      } else if (domainEvents.hasEventMatching(MoneyWithdrawalFailedDueToRecentWithdrawal.class)) {
         return new FailedOperationResult(FailureReason.WITHDRAWAL_FAILED_DUE_TO_RECENT_WITHDRAWAL);
      } else if (domainEvents.hasEventMatching(MoneyWithdrawalFailedDueToLimitNotDefined.class)) {
         return new FailedOperationResult(FailureReason.WITHDRAWAL_FAILED_DUE_TO_LIMIT_NOT_DEFINE);
      } else if (domainEvents.hasEventMatching(MoneyWithdrawn.class)) {
         //TODO: verify success of event store operation
         updateBalance(balanceId, domainEvents);
         return BalanceOperationResult.success(balanceId);
      } else {
         return BalanceOperationResult.failure(FailureReason.UNKNOWN_FAILURE_REASON);
      }
   }

   @Override
   public BalanceOperationResult repay(UUID balanceId, BigDecimal amount) {
      Optional<Balance> currentBalance = loadBalance(balanceId);

      if (currentBalance.isEmpty()) {
         return BalanceOperationResult.failure(FailureReason.BALANCE_DOES_NOT_EXIST);
      }

      DomainEvents<Balance, BalanceId> domainEvents = currentBalance.get()
          .repay(amount, LocalDateTime.now());

      if (domainEvents.hasEventMatching(MoneyRepaid.class)) {
         //TODO: verify success of event store operation
         updateBalance(balanceId, domainEvents);
         return BalanceOperationResult.success(balanceId);
      } else {
         return BalanceOperationResult.failure(FailureReason.UNKNOWN_FAILURE_REASON);
      }

   }
}
