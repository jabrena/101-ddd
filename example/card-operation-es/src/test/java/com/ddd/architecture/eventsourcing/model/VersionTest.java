package com.ddd.architecture.eventsourcing.model;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class VersionTest {

   @Test
   void initialVersion() {
      assertThat(Version.initialVersion().version()).isEqualTo(0L);
   }

   @Test
   void version() {
      assertThat(Version.version(3L).version()).isEqualTo(3L);
   }

   @Test
   void nextVersion() {
      assertThat(Version.version(3L).nextVersion().version()).isEqualTo(4L);
   }
}
