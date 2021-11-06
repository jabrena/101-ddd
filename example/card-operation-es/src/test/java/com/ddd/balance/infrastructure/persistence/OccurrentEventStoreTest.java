package com.ddd.balance.infrastructure.persistence;

import com.ddd.architecture.eventsourcing.eventstore.StaleAggregateVersionException;
import com.ddd.architecture.eventsourcing.model.DomainEvents;
import com.ddd.balance.domain.model.entity.Balance;
import com.ddd.balance.domain.model.entity.BalanceId;
import com.ddd.balance.domain.model.event.BalanceCreated;
import com.ddd.balance.domain.model.event.MoneyWithdrawn;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.ddd.architecture.eventsourcing.model.Version.initialVersion;
import static com.ddd.architecture.eventsourcing.model.Version.version;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;

@SpringBootTest
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class OccurrentEventStoreTest extends EventStoreAbstractIntegrationTest {
    protected final UUID balanceId = UUID.randomUUID();
    protected final UUID customerId = UUID.randomUUID();

    @Nested
    class Given_I_have_domain_events {

        protected final InMemoryEventStore<Balance, BalanceId> eventStore = new InMemoryEventStore<>();

        protected final DomainEvents<Balance, BalanceId> events;
        protected final LocalDateTime withdrawalTime = LocalDateTime.now();

        public Given_I_have_domain_events() {
            events = new DomainEvents<>(new BalanceId(balanceId), initialVersion(), List.of(
                    new BalanceCreated(balanceId, customerId, BigDecimal.valueOf(100.50), version(1)),
                    new MoneyWithdrawn(balanceId, BigDecimal.valueOf(20.3), withdrawalTime,
                            version(2))
            ));
        }

        @Nested
        class When_I_save_a_new_aggregate {

            public When_I_save_a_new_aggregate() {
                eventStore.saveNewAggregate(new BalanceId(balanceId), events);
            }

            @Test
            void then_I_can_load_those_events_from_the_store() {
                Optional<DomainEvents<Balance, BalanceId>> domainEvents = eventStore
                        .loadEvents(new BalanceId(balanceId));

                assertThat(domainEvents).isNotEmpty();
                assertThat(domainEvents.get()).containsExactly(
                        new BalanceCreated(balanceId, customerId, BigDecimal.valueOf(100.50), version(1)),
                        new MoneyWithdrawn(balanceId, BigDecimal.valueOf(20.3), withdrawalTime,
                                version(2))

                );
            }
        }
    }

    @Nested
    class Given_I_have_domain_events_saved {

        protected final InMemoryEventStore<Balance, BalanceId> eventStore = new InMemoryEventStore<>();
        private final LocalDateTime firstWithdrawalTime = LocalDateTime.now().minus(1, ChronoUnit.DAYS);
        private final LocalDateTime secondWithdrawalTime = LocalDateTime.now();

        public Given_I_have_domain_events_saved() {
            DomainEvents<Balance, BalanceId> events = new DomainEvents<>(
                    new BalanceId(balanceId),
                    initialVersion(),
                    List.of(
                            new BalanceCreated(balanceId, customerId, BigDecimal.valueOf(100.50), version(1)),
                            new MoneyWithdrawn(balanceId, BigDecimal.valueOf(20.3), firstWithdrawalTime,
                                    version(2))
                    ));
            eventStore.saveNewAggregate(new BalanceId(balanceId), events);
        }

        @Nested
        class When_I_update {

            public When_I_update() {
                DomainEvents<Balance, BalanceId> events = new DomainEvents<>(
                        new BalanceId(balanceId),
                        version(2),
                        List.of(
                                new MoneyWithdrawn(balanceId, BigDecimal.valueOf(20.3), secondWithdrawalTime,
                                        version(3))
                        )
                );
                eventStore.updateExistingAggregate(new BalanceId(balanceId), events);
            }

            @Test
            void then_I_can_load_all_events() {
                Optional<DomainEvents<Balance, BalanceId>> domainEvents = eventStore
                        .loadEvents(new BalanceId(balanceId));

                assertThat(domainEvents).isNotEmpty();

                DomainEvents<Balance, BalanceId> expectedDomainEvents = new DomainEvents<>(
                        new BalanceId(balanceId),
                        version(3),
                        List.of(
                                new BalanceCreated(balanceId, customerId, BigDecimal.valueOf(100.50),
                                        version(1)),
                                new MoneyWithdrawn(balanceId, BigDecimal.valueOf(20.3), firstWithdrawalTime, version(2)),
                                new MoneyWithdrawn(balanceId, BigDecimal.valueOf(20.3), secondWithdrawalTime, version(3))
                        ));

                assertThat(domainEvents.get()).isEqualTo(
                        expectedDomainEvents
                );
            }
        }

        @Nested
        class When_I_update_with_stale_version {

            private final DomainEvents<Balance, BalanceId> events;

            public When_I_update_with_stale_version() {
                events = new DomainEvents<>(
                        new BalanceId(balanceId),
                        version(1),
                        List.of(
                                new MoneyWithdrawn(balanceId, BigDecimal.valueOf(20.3), LocalDateTime.now(), version(3))
                        )
                );
            }

            @Test
            void then_I_should_receive_an_error_saying_persisted_version_is_more_recent() {
                BalanceId aggregateId = new BalanceId(balanceId);
                StaleAggregateVersionException exception = catchThrowableOfType(
                        () -> eventStore.updateExistingAggregate(
                                aggregateId,
                                events
                        ),
                        StaleAggregateVersionException.class
                );

                assertThat(exception.getAggregateVersion()).isEqualTo(2);
                assertThat(exception.getPresentedVersion()).isEqualTo(1);
                assertThat(exception.getAggregateName()).isEqualTo(aggregateId.getAggregateName());
            }

        }
    }
}
