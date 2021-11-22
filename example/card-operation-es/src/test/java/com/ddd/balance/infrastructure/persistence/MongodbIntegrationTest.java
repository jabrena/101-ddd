package com.ddd.balance.infrastructure.persistence;

import com.ddd.balance.infrastructure.configuration.EventStoreConfiguration;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.MongoDBContainer;

@SpringBootTest
@DisplayName("MongoDB integration tests")
@ExtendWith(SpringExtension.class)
@EnableMongoRepositories
@ContextConfiguration(
    initializers = MongodbIntegrationTest.Initializer.class,
    classes = {
        EventStoreConfiguration.class
    }
)
public abstract class MongodbIntegrationTest {

   private static final Integer MONGO_PORT = 27017;
   private static final String MONGO_DOCKER_IMAGE = "mongo:5.0.3";

   protected static MongoDBContainer mongo;

   @BeforeAll
   static void setup() {
      if (mongo == null) {
         mongo = startMongo();
      } else if (!mongo.isRunning()) {
         mongo = startMongo();
      }
   }

   private static MongoDBContainer startMongo() {
      MongoDBContainer mongo = new MongoDBContainer(MONGO_DOCKER_IMAGE)
          .withExposedPorts(MONGO_PORT)
          .withReuse(true);

      mongo.start();
      return mongo;
   }

   @AfterAll
   static void tearDown() {
   }

   public static class Initializer
       implements ApplicationContextInitializer<ConfigurableApplicationContext> {

      @Override
      public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
         TestPropertyValues values =
             TestPropertyValues.of(
                 "spring.mongo.host=" + mongo.getContainerIpAddress(),
                 "spring.mongo.port=" + mongo.getMappedPort(MONGO_PORT));

         values.applyTo(configurableApplicationContext);
      }
   }

}
