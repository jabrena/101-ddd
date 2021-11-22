package com.ddd.balance.infrastructure.persistence;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.occurrent.eventstore.mongodb.spring.blocking.SpringMongoEventStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

@SpringBootTest
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public abstract class EventStoreAbstractIntegrationTest extends MongodbIntegrationTest {

    @Autowired
    protected SpringMongoEventStore springMongoEventStore;

    @Autowired
    protected PlatformTransactionManager transactionManager;

    protected TransactionTemplate transactionTemplate;

    @BeforeEach
    void setUp() {
        transactionTemplate = new TransactionTemplate(transactionManager);
    }
}
