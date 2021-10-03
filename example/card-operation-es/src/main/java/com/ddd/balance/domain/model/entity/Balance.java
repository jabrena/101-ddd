package com.ddd.balance.domain.model.entity;

import static com.ddd.architecture.eventsourcing.model.DomainEventVersionGenerator.domainEventVersionGenerator;
import static io.vavr.collection.List.ofAll;

import com.ddd.architecture.eventsourcing.eventstore.EventStream;
import com.ddd.architecture.eventsourcing.model.DomainEvent;
import com.ddd.architecture.eventsourcing.model.DomainEventVersionGenerator;
import com.ddd.architecture.eventsourcing.model.DomainEvents;
import com.ddd.architecture.eventsourcing.model.EventSourcedAggregateRoot;
import com.ddd.architecture.eventsourcing.model.Version;
import com.ddd.architecture.eventsourcing.model.Versioned;
import com.ddd.balance.domain.model.event.BalanceCreated;
import com.ddd.balance.domain.model.event.MoneyBalanceLimitAlreadyDefined;
import com.ddd.balance.domain.model.event.MoneyBalanceLimitDefined;
import com.ddd.balance.domain.model.event.MoneyRepaid;
import com.ddd.balance.domain.model.event.MoneyWithdrawalFailedDueToInsufficientBalance;
import com.ddd.balance.domain.model.event.MoneyWithdrawalFailedDueToLimitNotDefined;
import com.ddd.balance.domain.model.event.MoneyWithdrawalFailedDueToRecentWithdrawal;
import com.ddd.balance.domain.model.event.MoneyWithdrawn;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import org.jmolecules.ddd.types.AggregateRoot;

public class Balance implements EventSourcedAggregateRoot<Balance, BalanceId>,
    AggregateRoot<Balance, BalanceId>, Versioned {

   private final BalanceId balanceId;
   private UUID customerId;
   private BigDecimal balance;
   private LocalDateTime lastWithdrawalTime;
   private BigDecimal balanceLimit;
   public final Version version;

   private Balance(BalanceId balanceId, Version version) {
      this.balanceId = balanceId;
      this.version = version;
   }

   public Balance(BalanceId balanceId, UUID customerId, BigDecimal balance, Version version) {
      this(balanceId, version);
      this.customerId = customerId;
      this.balance = balance;
      this.balanceLimit = null;
      this.lastWithdrawalTime = null;
   }

   public Balance(BalanceId balanceId, UUID customerId, BigDecimal balance,
       LocalDateTime lastWithdrawalTime, Version version) {
      this(balanceId, customerId, balance, version);
      this.lastWithdrawalTime = lastWithdrawalTime;
   }

   public Balance(BalanceId balanceId, UUID customerId, BigDecimal balance,
       LocalDateTime lastWithdrawalTime, BigDecimal balanceLimit, Version version) {
      this(balanceId, customerId, balance,
          lastWithdrawalTime, version);
      this.balanceLimit = balanceLimit;
   }

   @Override
   public Version getVersion() {
      return version;
   }

   @Override
   public BalanceId getId() {
      return balanceId;
   }

   public UUID getCustomerId() {
      return customerId;
   }

   public BigDecimal getBalance() {
      return balance;
   }

   public static DomainEvents<Balance, BalanceId> createBalance(BalanceId balanceId,
       UUID customerId,
       BigDecimal originalBalance) {
      DomainEventVersionGenerator versionGenerator = domainEventVersionGenerator(
          Version.initialVersion());
      return new DomainEvents<>(balanceId, versionGenerator.getAggregateVersion(),
          List.of(
              new BalanceCreated(balanceId.id(), customerId, originalBalance,
                  versionGenerator.nextVersion())
          ));
   }

   public static Balance reHydrate(EventStream<Balance, BalanceId> eventStream) {
      return ofAll(eventStream)
          .foldLeft(new Balance(eventStream.getAggregateId(), Version.initialVersion()),
              Balance::apply);
   }

   public Balance apply(DomainEvent<Balance> event) {
      if (event instanceof BalanceCreated) {
         return apply((BalanceCreated) event);
      } else if (event instanceof MoneyWithdrawn) {
         return apply((MoneyWithdrawn) event);
      } else if (event instanceof MoneyRepaid) {
         return apply((MoneyRepaid) event);
      } else if (event instanceof MoneyBalanceLimitDefined) {
         return apply((MoneyBalanceLimitDefined) event);
      } else if (event instanceof MoneyWithdrawalFailedDueToInsufficientBalance) {
         return this;
      } else if (event instanceof MoneyWithdrawalFailedDueToRecentWithdrawal) {
         return this;
      } else if (event instanceof MoneyBalanceLimitAlreadyDefined) {
         return this;
      } else if (event instanceof MoneyWithdrawalFailedDueToLimitNotDefined) {
         return this;
      } else {
         throw new IllegalArgumentException(
             String.format("This event %s is not supported", event));
      }
   }

   public Balance apply(BalanceCreated event) {
      return new Balance(balanceId, event.customerId(), event.originalBalance(), event.version());
   }

   public Balance apply(MoneyWithdrawn event) {
      return new Balance(balanceId, customerId, balance.subtract(event.amount()),
          event.withdrawalTime(), this.balanceLimit, event.version());
   }

   public Balance apply(MoneyRepaid event) {
      return new Balance(balanceId, customerId, balance.add(event.amount()),
          event.repayTime(), this.balanceLimit, event.version());
   }

   public Balance apply(MoneyBalanceLimitDefined event) {
      return new Balance(balanceId, customerId, balance, lastWithdrawalTime, event.balanceLimit(),
          event.version());
   }

   public DomainEvents<Balance, BalanceId> defineBalanceLimit(BigDecimal balanceLimit) {
      DomainEventVersionGenerator versionGenerator = domainEventVersionGenerator(version);

      if (isBalanceLimitDefined()) {
         return new DomainEvents<>(balanceId, versionGenerator.getAggregateVersion(),
             new MoneyBalanceLimitAlreadyDefined(balanceId.id(), balanceLimit,
                 versionGenerator.nextVersion())
         );
      }

      return new DomainEvents<>(balanceId, versionGenerator.getAggregateVersion(),
          new MoneyBalanceLimitDefined(balanceId.id(), balanceLimit, versionGenerator.nextVersion())
      );
   }

   public DomainEvents<Balance, BalanceId> withdraw(BigDecimal amount,
       LocalDateTime withdrawalTime) {
      DomainEventVersionGenerator versionGenerator = domainEventVersionGenerator(version);

      if (isBalanceLimitNotDefined()) {
         return new DomainEvents<>(balanceId, versionGenerator.getAggregateVersion(),
             new MoneyWithdrawalFailedDueToLimitNotDefined(balanceId.id(), amount,
                 withdrawalTime, versionGenerator.nextVersion())
         );
      }

      if (isLastWithdrawalMoreThanOneHourAgo(withdrawalTime)) {
         return new DomainEvents<>(balanceId, versionGenerator.getAggregateVersion(),
             new MoneyWithdrawalFailedDueToRecentWithdrawal(balanceId.id(), amount,
                 withdrawalTime, lastWithdrawalTime, versionGenerator.nextVersion())
         );
      }

      if (isBalanceInsufficientForWithdraw(amount)) {
         return new DomainEvents<>(balanceId, versionGenerator.getAggregateVersion(),
             new MoneyWithdrawalFailedDueToInsufficientBalance(balanceId.id(), withdrawalTime,
                 amount, versionGenerator.nextVersion())
         );
      }

      return new DomainEvents<>(balanceId, versionGenerator.getAggregateVersion(),
          List.of(
              new MoneyWithdrawn(balanceId.id(), amount, withdrawalTime,
                  versionGenerator.nextVersion())
          ));
   }

   public DomainEvents<Balance, BalanceId> repay(BigDecimal amount, LocalDateTime repayTime) {
      DomainEventVersionGenerator versionGenerator = domainEventVersionGenerator(version);
      return new DomainEvents<>(balanceId, versionGenerator.getAggregateVersion(),
          List.of(
              new MoneyRepaid(balanceId.id(), amount, repayTime,
                  versionGenerator.nextVersion())
          ));
   }


   private boolean isBalanceLimitDefined() {
      return this.balanceLimit != null;
   }

   private boolean isBalanceLimitNotDefined() {
      return !isBalanceLimitDefined();
   }

   private boolean isBalanceInsufficientForWithdraw(BigDecimal amount) {
      return balanceLimit == null || balance.subtract(amount).compareTo(balanceLimit) < 0;
   }

   private boolean isLastWithdrawalMoreThanOneHourAgo(LocalDateTime withdrawalTime) {
      return lastWithdrawalTime != null && withdrawalTime.isBefore(
          lastWithdrawalTime.plus(1, ChronoUnit.HOURS));
   }

   public BigDecimal getBalanceLimit() {
      return balanceLimit;
   }
}
