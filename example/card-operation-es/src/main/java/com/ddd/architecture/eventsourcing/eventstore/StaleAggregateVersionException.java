package com.ddd.architecture.eventsourcing.eventstore;


import com.ddd.architecture.eventsourcing.model.Version;

public class StaleAggregateVersionException extends RuntimeException {

   private final Long aggregateVersion;
   private final Long presentedVersion;
   private final String aggregateName;

   public StaleAggregateVersionException(
       String aggregateName,
       Version aggregateVersion,
       Version presentedVersion
   ) {
      super(
          "Aggregate %s is currently persisted with events till version %s.  We can not append events generated with version %s of that aggregate.".formatted(
              aggregateName, aggregateVersion, presentedVersion));
      this.aggregateVersion = aggregateVersion.version();
      this.presentedVersion = presentedVersion.version();
      this.aggregateName = aggregateName;
   }

   public Long getAggregateVersion() {
      return aggregateVersion;
   }

   public Long getPresentedVersion() {
      return presentedVersion;
   }

   public String getAggregateName() {
      return aggregateName;
   }
}
