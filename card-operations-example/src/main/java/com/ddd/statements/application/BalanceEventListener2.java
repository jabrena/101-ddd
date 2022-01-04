package com.ddd.statements.application;

import com.ddd.balance.domain.model.Balance;
import com.ddd.balance.domain.model.MoneyWithDrewEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.context.event.EventListener;

@Component
public class BalanceEventListener2 {

    Logger logger = LoggerFactory.getLogger(BalanceEventListener2.class);

    @EventListener
    public void addStatementNegative(MoneyWithdrewEventStep2 event) {
        Balance source = event.getSource();
        logger.info("New Event Received: MoneyWithdrewEventStep2 {}", source);
        //throw new RuntimeException("Katakroker");
    }
}
