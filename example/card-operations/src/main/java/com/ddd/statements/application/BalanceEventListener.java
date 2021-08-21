package com.ddd.statements.application;

import com.ddd.balance.domain.model.Balance;
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
    public void addStatement(MoneyWithDrewEvent event) {
        Balance source = event.getSource();
        logger.info("New Event Received: {}", source);
    }

}
