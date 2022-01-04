package com.ddd.statements.application;

import com.ddd.balance.domain.model.Balance;
import com.ddd.balance.domain.model.MoneyWithDrewEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;

@Component
public class BalanceEventListener {

    Logger logger = LoggerFactory.getLogger(BalanceEventListener.class);

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    @EventListener
    public void addStatementNegative(MoneyWithDrewEvent event) {
        Balance source = event.getSource();
        logger.info("New Event Received: MoneyWithDrewEvent {}", source);
        //throw new RuntimeException("Katakroker");

        applicationEventPublisher.publishEvent(new MoneyWithdrewEventStep2(event.getSource()));
    }
}
