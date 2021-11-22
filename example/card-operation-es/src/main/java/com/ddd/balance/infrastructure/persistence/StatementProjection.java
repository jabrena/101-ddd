package com.ddd.balance.infrastructure.persistence;


import com.ddd.architecture.eventsourcing.model.DomainEvent;
import com.ddd.balance.domain.model.entity.Balance;
import com.ddd.balance.domain.model.event.MoneyRepaid;
import com.ddd.balance.domain.model.event.MoneyWithdrawn;
import org.occurrent.application.converter.CloudEventConverter;
import org.occurrent.subscription.api.blocking.SubscriptionModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.Duration;

import static java.time.temporal.ChronoUnit.SECONDS;

@Component
public class StatementProjection {
    private static Logger LOGGER = LoggerFactory.getLogger(StatementProjection.class);

    private final SubscriptionModel subscription;
    private final CloudEventConverter<DomainEvent<Balance>> domainEventCloudEventConverter;

    public StatementProjection(SubscriptionModel subscription,
                               CloudEventConverter<DomainEvent<Balance>> balanceCloudEventConverter) {
        this.subscription = subscription;
        domainEventCloudEventConverter = balanceCloudEventConverter;
    }

    @PostConstruct
    void startProjectionUpdater() {
        subscription.subscribe("current-name", cloudEvent -> {
                    DomainEvent<Balance> domainEvent = domainEventCloudEventConverter.toDomainEvent(cloudEvent);
                    switch (domainEvent) {
                        case MoneyRepaid moneyRepaid -> LOGGER.info("Money repaid: {}", moneyRepaid);
                        case MoneyWithdrawn moneyWithdrawn -> LOGGER.info("Money withdrawn: {}", moneyWithdrawn);
                        default -> {
                        }
                    }
                })
                .waitUntilStarted(Duration.of(2, SECONDS));
    }

}
