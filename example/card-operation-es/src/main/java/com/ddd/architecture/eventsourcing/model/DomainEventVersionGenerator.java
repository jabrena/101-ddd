package com.ddd.architecture.eventsourcing.model;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class DomainEventVersionGenerator {
   private final Version aggregateVersion;
   private Version nextEventVersion;

   public DomainEventVersionGenerator(Version aggregateVersion) {
      this.aggregateVersion = aggregateVersion;
      this.nextEventVersion = aggregateVersion;
   }

   public static DomainEventVersionGenerator domainEventVersionGenerator(Version aggregateVersion) {
      return new DomainEventVersionGenerator(aggregateVersion);
   }

   public Version nextVersion() {
      nextEventVersion = nextEventVersion.nextVersion();
      return nextEventVersion;
   }

   public Version getAggregateVersion() {
      return aggregateVersion;
   }

   @Override
   public String toString() {
      return new ToStringBuilder(this)
          .append("fromAggregateVersion", aggregateVersion)
          .append("nextEventVersion", nextEventVersion)
          .toString();
   }
}
