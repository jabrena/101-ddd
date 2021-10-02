package com.ddd.architecture.eventsourcing.model;

import com.ddd.architecture.eventsourcing.eventstore.EventStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;
import javax.validation.constraints.NotNull;

public record DomainEvents<T extends EventSourcedAggregateRoot<T, ID>, ID extends AggregateIdentifier>(
    ID aggregateId,
    Version fromAggregateVersion,
    @NotNull List<DomainEvent<T>> events
) implements EventStream<T, ID> {

   @SafeVarargs
   public DomainEvents(ID aggregateId, Version fromAggregateVersion, DomainEvent<T>... events) {
      this(aggregateId, fromAggregateVersion, List.of(events));
   }

   public DomainEvents<T, ID> withEvents(DomainEvents<T, ID> otherEvents) {
      List<DomainEvent<T>> allEvents = new ArrayList<>(events);
      allEvents.addAll(otherEvents.events);

      Version newAggregateVersion = lastEventVersion(allEvents);
      return new DomainEvents<>(aggregateId, newAggregateVersion,
          Collections.unmodifiableList(allEvents));
   }

   private Version lastEventVersion(List<DomainEvent<T>> events) {
      return events.stream()
          .reduce(fromAggregateVersion, (version, tDomainEvent) -> tDomainEvent.getVersion(),
              (version, version2) -> version2);
   }

   public Version getFromAggregateVersion() {
      return fromAggregateVersion;
   }

   public Version getToAggregateVersion() {
      return lastEventVersion(events);
   }

   @Override
   public String getAggregateName() {
      return aggregateId.getAggregateName();
   }

   @Override
   public ID getAggregateId() {
      return aggregateId;
   }

   @Override
   public Iterator<DomainEvent<T>> iterator() {
      return this.events.iterator();
   }

   public boolean hasEventMatching(Predicate<DomainEvent<T>> predicate) {
      return events.stream().anyMatch(predicate);
   }
   public boolean hasEventMatching(Class<? extends DomainEvent<T>> eventClass) {
      return hasEventMatching(balanceDomainEvent -> balanceDomainEvent.getClass().equals(
          eventClass));
   }
}
