package com.ddd.balance.domain.model.entity;

import static org.assertj.core.api.Assertions.assertThat;

import com.ddd.architecture.eventsourcing.eventstore.EventStore;
import com.ddd.architecture.eventsourcing.model.DomainEvents;
import com.ddd.architecture.eventsourcing.model.Version;
import com.ddd.balance.domain.model.event.BalanceCreated;
import com.ddd.balance.domain.model.event.MoneyRepaid;
import com.ddd.balance.domain.model.event.MoneyWithdrawalFailedDueToInsufficientBalance;
import com.ddd.balance.domain.model.event.MoneyWithdrawalFailedDueToLimitNotDefined;
import com.ddd.balance.domain.model.event.MoneyWithdrawalFailedDueToRecentWithdrawal;
import com.ddd.balance.domain.model.event.MoneyWithdrawn;
import com.ddd.balance.infrastructure.persistence.InMemoryEventStore;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class BalanceTest {

   private final BalanceId balanceId = new BalanceId(UUID.randomUUID());
   private final UUID customerId = UUID.randomUUID();

   @Nested
   class when_a_balance_is_created {

      private final DomainEvents<Balance, BalanceId> domainEvents;

      when_a_balance_is_created() {
         domainEvents = Balance.createBalance(balanceId, customerId, BigDecimal.valueOf(100.50));
      }

      @Test
      void then_a_CartCreatedEvent_is_emitted() {
         assertThat(domainEvents.events()).hasSize(1);
         assertThat(domainEvents.events()).containsExactly(
             new BalanceCreated(balanceId.id(), customerId, BigDecimal.valueOf(100.50),
                 new Version(1))
         );
      }
   }

   @Nested
   class when_I_apply_BalanceCreated_I_get_a_balance {

      private final Balance balance;


      public when_I_apply_BalanceCreated_I_get_a_balance() {
         EventStore<Balance, BalanceId> eventStore = new InMemoryEventStore<>();
         eventStore.saveNewAggregate(balanceId,
             Balance.createBalance(balanceId, customerId, BigDecimal.valueOf(100.50)));
         balance = Balance.reHydrate(eventStore.loadEvents(balanceId).orElseThrow());
      }

      @Test
      void then_balance_is_rehydrated() {
         assertThat(balance.getId()).isEqualTo(balanceId);
         assertThat(balance.getCustomerId()).isEqualTo(customerId);
         assertThat(balance.getBalance()).isEqualTo(BigDecimal.valueOf(100.50));
      }
   }

   @Nested
   class when_I_have_a_balance_of_100 {

      private Balance balance;
      private final EventStore<Balance, BalanceId> eventStore = new InMemoryEventStore<>();

      public when_I_have_a_balance_of_100() {
         eventStore.saveNewAggregate(balanceId,
             Balance.createBalance(balanceId, customerId, BigDecimal.valueOf(100.00)));
         balance = Balance.reHydrate(eventStore.loadEvents(balanceId).orElseThrow());
      }

      @Nested
      class and_balance_limit_is_not_defined {

         @Test
         void then_I_should_not_be_able_to_withdraw_money() {
            LocalDateTime withdrawalTime = LocalDateTime.now();
            DomainEvents<Balance, BalanceId> domainEvents = balance.withdraw(BigDecimal.valueOf(45.0),
                withdrawalTime);
            eventStore.updateExistingAggregate(balanceId, domainEvents);
            balance = Balance.reHydrate(eventStore.loadEvents(balanceId).orElseThrow());

            assertThat(domainEvents).isEqualTo(
                new DomainEvents<>(balanceId, Version.version(1),
                    new MoneyWithdrawalFailedDueToLimitNotDefined(balanceId.id(), BigDecimal.valueOf(45.0),
                        withdrawalTime,
                        Version.version(2))
                )
            );

            assertThat(balance.getBalance()).isEqualTo(BigDecimal.valueOf(100.0));
         }
      }

      @Nested
      class and_balance_limit_of_minus_50_is_defined {

         private LocalDateTime withdrawalTime;

         public and_balance_limit_of_minus_50_is_defined() {
            BigDecimal balanceLimit = BigDecimal.valueOf(-50.0);
            DomainEvents<Balance, BalanceId> domainEvents = balance.defineBalanceLimit(balanceLimit);
            eventStore.updateExistingAggregate(balanceId, domainEvents);
            balance = Balance.reHydrate(eventStore.loadEvents(balanceId).orElseThrow());
         }

         @Nested
         class and_I_want_to_withdraw_45 {

            private final DomainEvents<Balance, BalanceId> domainEvents;
            private final LocalDateTime withdrawalTime = LocalDateTime.now();

            public and_I_want_to_withdraw_45() {
               domainEvents = balance.withdraw(BigDecimal.valueOf(45.0), withdrawalTime);
               eventStore.updateExistingAggregate(balanceId, domainEvents);
               balance = Balance.reHydrate(eventStore.loadEvents(balanceId).orElseThrow());
            }

            @Test
            void then_I_should_have_a_balance_of_55() {
               assertThat(domainEvents).isEqualTo(
                   new DomainEvents<>(balanceId, Version.version(2),
                       new MoneyWithdrawn(balanceId.id(), BigDecimal.valueOf(45.0), withdrawalTime,
                           Version.version(3))
                   )
               );

               assertThat(balance.getBalance()).isEqualTo(BigDecimal.valueOf(55.0));
            }
         }

         @Nested
         class and_my_last_withdrawal_is_less_than_one_hour_ago {

            private DomainEvents<Balance, BalanceId> domainEvents;
            private final LocalDateTime firstWithdrawalTime = LocalDateTime.of(2021, 8, 19, 19, 55, 32);

            public and_my_last_withdrawal_is_less_than_one_hour_ago() {
               domainEvents = balance.withdraw(BigDecimal.valueOf(45.0), firstWithdrawalTime);
               eventStore.updateExistingAggregate(balanceId, domainEvents);
               balance = Balance.reHydrate(eventStore.loadEvents(balanceId).orElseThrow());
            }

            @Test
            void then_I_should_not_be_able_do_withdraw_anymore() {
               LocalDateTime secondWithdrawalTime = firstWithdrawalTime.plusMinutes(30);
               domainEvents = balance.withdraw(BigDecimal.valueOf(1.0), secondWithdrawalTime);

               assertThat(domainEvents).isEqualTo(
                   new DomainEvents<>(balanceId, Version.version(3),
                       new MoneyWithdrawalFailedDueToRecentWithdrawal(balanceId.id(),
                           BigDecimal.valueOf(1.0),
                           secondWithdrawalTime, firstWithdrawalTime, Version.version(4))
                   )
               );
            }
         }

         @Nested
         class and_my_last_withdrawal_is_more_than_one_hour_ago {

            private DomainEvents<Balance, BalanceId> domainEvents;
            private final LocalDateTime firstWithdrawalTime = LocalDateTime.of(2021, 8, 19, 19, 55, 32);

            public and_my_last_withdrawal_is_more_than_one_hour_ago() {
               domainEvents = balance.withdraw(BigDecimal.valueOf(45.0), firstWithdrawalTime);
               eventStore.updateExistingAggregate(balanceId, domainEvents);
               balance = Balance.reHydrate(eventStore.loadEvents(balanceId).orElseThrow());
            }

            @Test
            void then_I_should_not_be_able_do_withdraw_anymore() {
               LocalDateTime secondWithdrawalTime = firstWithdrawalTime.plusMinutes(61);
               domainEvents = balance.withdraw(BigDecimal.valueOf(1.0), secondWithdrawalTime);

               assertThat(domainEvents).isEqualTo(
                   new DomainEvents<>(balanceId, Version.version(3),
                       new MoneyWithdrawn(balanceId.id(), BigDecimal.valueOf(1.0),
                           secondWithdrawalTime, Version.version(4))
                   )
               );
            }
         }

         @Nested
         class and_I_want_to_withdraw_151 {

            private final DomainEvents<Balance, BalanceId> domainEvents;
            private final LocalDateTime withdrawalTime;

            public and_I_want_to_withdraw_151() {
               withdrawalTime = LocalDateTime.now();
               domainEvents = balance.withdraw(BigDecimal.valueOf(151.0), withdrawalTime);
               eventStore.updateExistingAggregate(balanceId, domainEvents);
               balance = Balance.reHydrate(eventStore.loadEvents(balanceId).orElseThrow());
            }

            @Test
            void then_I_should_not_be_able_to_withdraw_because_of_insufficient_balance() {
               assertThat(domainEvents).isEqualTo(
                   new DomainEvents<>(balanceId, Version.version(2),
                       new MoneyWithdrawalFailedDueToInsufficientBalance(balanceId.id(),
                           withdrawalTime, BigDecimal.valueOf(151.0),
                           Version.version(3))
                   )
               );

               assertThat(balance.getBalance()).isEqualTo(BigDecimal.valueOf(100.0));
            }
         }


         @Test
         void then_I_should_not_be_able_to_withdraw_money_lower_than_balance_limit() {
            withdrawalTime = LocalDateTime.now();
            DomainEvents<Balance, BalanceId> domainEvents = balance.withdraw(BigDecimal.valueOf(151.0),
                withdrawalTime);
            eventStore.updateExistingAggregate(balanceId, domainEvents);
            balance = Balance.reHydrate(eventStore.loadEvents(balanceId).orElseThrow());

            assertThat(domainEvents).isEqualTo(
                new DomainEvents<>(balanceId, Version.version(2),
                    new MoneyWithdrawalFailedDueToInsufficientBalance(balanceId.id(),
                        withdrawalTime, BigDecimal.valueOf(151.0),
                                            Version.version(3))
                )
            );

            assertThat(balance.getBalance()).isEqualTo(BigDecimal.valueOf(100.0));
         }
         @Test
         void then_I_should_be_able_to_withdraw_money_equal_to_balance_limit() {
            withdrawalTime = LocalDateTime.now();
            DomainEvents<Balance, BalanceId> domainEvents = balance.withdraw(BigDecimal.valueOf(150.0),
                withdrawalTime);
            eventStore.updateExistingAggregate(balanceId, domainEvents);
            balance = Balance.reHydrate(eventStore.loadEvents(balanceId).orElseThrow());

            assertThat(domainEvents).isEqualTo(
                new DomainEvents<>(balanceId, Version.version(2),
                    new MoneyWithdrawn(balanceId.id(),
                        BigDecimal.valueOf(150.0), withdrawalTime,
                                                                Version.version(3))
                )
            );

            assertThat(balance.getBalance()).isEqualTo(BigDecimal.valueOf(-50.0));
         }
      }

      @Nested
      class and_I_repay_42 {

         private final DomainEvents<Balance, BalanceId> domainEvents;
         private final LocalDateTime repayTime = LocalDateTime.now();

         public and_I_repay_42() {
            domainEvents = balance.repay(BigDecimal.valueOf(42.0), repayTime);
            eventStore.updateExistingAggregate(balanceId, domainEvents);
            balance = Balance.reHydrate(eventStore.loadEvents(balanceId).orElseThrow());
         }

         @Test
         void then_I_should_have_a_balance_of_142() {
            assertThat(domainEvents).isEqualTo(
                new DomainEvents<>(balanceId, Version.version(1),
                    new MoneyRepaid(balanceId.id(), BigDecimal.valueOf(42.0), repayTime,
                        Version.version(2))
                )
            );

            assertThat(balance.getBalance()).isEqualTo(BigDecimal.valueOf(142.0));
         }
      }

   }

}
