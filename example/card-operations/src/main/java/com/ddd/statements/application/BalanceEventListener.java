package com.ddd.statements.application;

import com.ddd.balance.domain.model.Balance;
import com.ddd.balance.domain.model.MoneyRepaidEvent;
import com.ddd.balance.domain.model.MoneyWithDrewEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;

import static org.springframework.transaction.annotation.Propagation.REQUIRES_NEW;

@Component
public class BalanceEventListener {

    Logger logger = LoggerFactory.getLogger(BalanceEventListener.class);

    @TransactionalEventListener
    @Transactional(propagation = REQUIRES_NEW)
    public void addStatementNegative(MoneyWithDrewEvent event) {
        Balance source = event.getSource();
        logger.info("New Event Received: {}", source);
    }

    @TransactionalEventListener
    @Transactional(propagation = REQUIRES_NEW)
    public void addStatementPositive(MoneyRepaidEvent event) {
        Balance source = event.getSource();
        logger.info("New Event Received: {}", source);
    }

}
