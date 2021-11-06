package com.ddd.balance.infrastructure.persistence;

import com.ddd.architecture.Adapter;
import com.ddd.architecture.eventsourcing.eventstore.EventStore;
import com.ddd.architecture.eventsourcing.eventstore.StaleAggregateVersionException;
import com.ddd.architecture.eventsourcing.model.DomainEvent;
import com.ddd.architecture.eventsourcing.model.DomainEvents;
import com.ddd.architecture.eventsourcing.model.Version;
import com.ddd.balance.domain.model.entity.Balance;
import com.ddd.balance.domain.model.entity.BalanceId;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cloudevents.CloudEvent;
import io.cloudevents.core.builder.CloudEventBuilder;
import org.occurrent.application.converter.CloudEventConverter;
import org.occurrent.application.converter.jackson.JacksonCloudEventConverter;
import org.occurrent.eventstore.api.WriteConditionNotFulfilledException;
import org.occurrent.eventstore.api.blocking.EventStream;
import org.occurrent.eventstore.mongodb.spring.blocking.SpringMongoEventStore;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static io.vavr.API.unchecked;

@Adapter
@Service
@Transactional(readOnly = true)
public class OccurrentEventStore implements EventStore<Balance, BalanceId> {
    private final SpringMongoEventStore springMongoEventStore;
    protected final PlatformTransactionManager transactionManager;
    private final ObjectMapper eventObjectMapper;

    public OccurrentEventStore(SpringMongoEventStore springMongoEventStore,
                               PlatformTransactionManager transactionManager,
                               ObjectMapper eventObjectMapper) {
        this.springMongoEventStore = springMongoEventStore;
        this.transactionManager = transactionManager;
        this.eventObjectMapper = eventObjectMapper;
    }

    @Override
    public Optional<DomainEvents<Balance, BalanceId>> loadEvents(BalanceId aggregateId) {
        Stream<DomainEvent<Balance>> retrievedDomainEvents = retrieveDomainEvents(getStreamId(aggregateId));
        List<DomainEvent<Balance>> domainEvents = retrievedDomainEvents.toList();
        return domainEvents.isEmpty() ? Optional.empty() :
                Optional.of(new DomainEvents<>(aggregateId, Version.initialVersion(), domainEvents));
    }

    private String getStreamId(BalanceId aggregateId) {
        return aggregateId.getAggregateName() + aggregateId.id();
    }

    private Stream<DomainEvent<Balance>> retrieveDomainEvents(String streamId) {
        EventStream<CloudEvent> read = springMongoEventStore.read(streamId, 0, 100);
        List<CloudEvent> cloudEvents = read.eventList();

        CloudEventConverter<DomainEvent<Balance>> domainEventCloudEventConverter = buildCartCloudEventConverter();

        return cloudEvents.stream()
                .map(domainEventCloudEventConverter::toDomainEvent);
    }

    private CloudEventConverter<DomainEvent<Balance>> buildCartCloudEventConverter() {
        URI cloudEventSource = URI.create("urn:be.xl:shopping:foo");

        return new JacksonCloudEventConverter.Builder<DomainEvent<Balance>>(eventObjectMapper, cloudEventSource).build();
    }

    @Override
    @Transactional
    public void saveNewAggregate(BalanceId aggregateId, DomainEvents<Balance, BalanceId> events) {
        String streamId = aggregateId.getAggregateName() + aggregateId.id();

        storeCloudEvents(streamId, serialize(events.events()));
    }

    private void storeCloudEvents(String streamId, List<CloudEvent> cloudEvents) {
        springMongoEventStore.write(streamId, cloudEvents.stream());
    }

    private List<CloudEvent> serialize(List<DomainEvent<Balance>> events) {
        return events.stream()
                .map(event ->
                                CloudEventBuilder.v1()
                                        .withId(UUID.randomUUID().toString()) //TODO: ?? aggregate Id in DomainEvent
                                        .withSource(URI.create("urn:be.xl:shopping:Balance"))
                                        .withType(event.getClass().getCanonicalName())
                                        .withData(unchecked(eventObjectMapper::writeValueAsBytes).apply(event))
                                        .build()
                ).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void updateExistingAggregate(BalanceId aggregateId, DomainEvents<Balance, BalanceId> events) throws StaleAggregateVersionException {
        try {
            springMongoEventStore.write(
                    getStreamId(aggregateId),
                    events.fromAggregateVersion().version(),
                    serialize(events.events()).stream());
        } catch (WriteConditionNotFulfilledException e) {
            //TODO improve with check on version
            throw new StaleAggregateVersionException(
                    aggregateId.getAggregateName(),
                    Version.version(e.eventStreamVersion),
                    events.fromAggregateVersion()
            );
        }
    }
}
