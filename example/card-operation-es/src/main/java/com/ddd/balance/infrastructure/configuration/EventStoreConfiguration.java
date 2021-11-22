package com.ddd.balance.infrastructure.configuration;

import com.ddd.architecture.eventsourcing.eventstore.EventStore;
import com.ddd.architecture.eventsourcing.model.DomainEvent;
import com.ddd.balance.domain.model.entity.Balance;
import com.ddd.balance.domain.model.entity.BalanceId;
import com.ddd.balance.infrastructure.persistence.OccurrentEventStore;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClients;
import org.occurrent.application.converter.CloudEventConverter;
import org.occurrent.application.converter.jackson.JacksonCloudEventConverter;
import org.occurrent.eventstore.mongodb.spring.blocking.EventStoreConfig;
import org.occurrent.eventstore.mongodb.spring.blocking.SpringMongoEventStore;
import org.occurrent.mongodb.timerepresentation.TimeRepresentation;
import org.occurrent.subscription.api.blocking.PositionAwareSubscriptionModel;
import org.occurrent.subscription.api.blocking.SubscriptionModel;
import org.occurrent.subscription.api.blocking.SubscriptionPositionStorage;
import org.occurrent.subscription.blocking.durable.DurableSubscriptionModel;
import org.occurrent.subscription.mongodb.spring.blocking.SpringMongoSubscriptionModel;
import org.occurrent.subscription.mongodb.spring.blocking.SpringMongoSubscriptionPositionStorage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.convert.DbRefResolver;
import org.springframework.data.mongodb.core.convert.DefaultDbRefResolver;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;

import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.List;

@Configuration
public class EventStoreConfiguration extends AbstractMongoClientConfiguration {

    private static final String EVENTS_COLLECTION_NAME = "events";

    @Value("${spring.mongo.host}")
    private String mongoHost;

    @Value("${spring.mongo.port}")
    private Integer mongoPort;

    @Value("${spring.mongo.databaseName}")
    private String databaseName;

    @Override
    @Bean
    public com.mongodb.client.MongoClient mongoClient() {
        ConnectionString connectionString = new ConnectionString(mongodbConnectionString());
        MongoClientSettings mongoClientSettings = MongoClientSettings.builder()
                .applyConnectionString(connectionString)
                .build();

        return MongoClients.create(mongoClientSettings);
    }

    private String mongodbConnectionString() {
        return String.format("mongodb://%s:%d/%s", mongoHost, mongoPort, databaseName);
    }

    @Bean
    public MongoTransactionManager transactionManager() {
        return new MongoTransactionManager(mongoDbFactory());
    }

    @Bean
    public SpringMongoEventStore springMongoEventStore() {
        EventStoreConfig eventStoreConfig = new EventStoreConfig.Builder()
                .eventStoreCollectionName(EVENTS_COLLECTION_NAME)
                .transactionConfig(transactionManager())
                .timeRepresentation(TimeRepresentation.DATE)
                .build();
        return new SpringMongoEventStore(
                mongoTemplate(mongoDbFactory(), mappingMongoConverter()),
                eventStoreConfig
        );
    }

    private MappingMongoConverter mappingMongoConverter() {
        DbRefResolver dbRefResolver = new DefaultDbRefResolver(mongoDbFactory());

        MongoCustomConversions conversions = new MongoCustomConversions(List.of());
        MongoMappingContext mappingContext = new MongoMappingContext();
        mappingContext.setSimpleTypeHolder(conversions.getSimpleTypeHolder());
        mappingContext.afterPropertiesSet();
        MappingMongoConverter converter = new MappingMongoConverter(dbRefResolver, mappingContext);
        return converter;
    }

    @Override
    protected String getDatabaseName() {
        return databaseName;
    }

    @Bean
    public SubscriptionModel subscription() {
        MongoTemplate mongoTemplate = mongoTemplate(mongoDbFactory(), mappingMongoConverter());

        // Create the blocking subscription
        return new SpringMongoSubscriptionModel(
                mongoTemplate,
                "CartEventStream",
                TimeRepresentation.DATE
        );
    }

    private static final String EVENTS_COLLECTION = "events";

    @Bean
    public PositionAwareSubscriptionModel positionAwareSubscriptionModel(MongoTemplate mongoTemplate) {
        return new SpringMongoSubscriptionModel(mongoTemplate, EVENTS_COLLECTION, TimeRepresentation.RFC_3339_STRING);
    }

    @Bean
    public SubscriptionPositionStorage storage(MongoTemplate mongoTemplate) {
        return new SpringMongoSubscriptionPositionStorage(mongoTemplate, "subscriptions");
    }

    @Primary
    @Bean
    public PositionAwareSubscriptionModel autoPersistingSubscriptionModel(
            PositionAwareSubscriptionModel subscription, SubscriptionPositionStorage storage) {
        return new DurableSubscriptionModel(subscription, storage);
    }

    @Primary
    @Bean
    public ObjectMapper eventObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        SimpleDateFormat rfc3339 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
        objectMapper.setDateFormat(rfc3339);
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return objectMapper;
    }

    @Bean
    public EventStore<Balance, BalanceId> balanceEventStore() {
        return new OccurrentEventStore(springMongoEventStore(), transactionManager(), eventObjectMapper());
    }

    @Bean
    public CloudEventConverter<DomainEvent<Balance>> balanceCloudEventConverter() {
        URI cloudEventSource = URI.create("urn:be.xl:shopping:foo");

        return new JacksonCloudEventConverter.Builder<DomainEvent<Balance>>(eventObjectMapper(), cloudEventSource).build();
    }
}
