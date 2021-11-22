package com.ddd.balance.domain.model;

import org.springframework.context.ApplicationEvent;

public class MoneyRepaidEvent extends ApplicationEvent {

    public MoneyRepaidEvent(Balance source) {
        super(source);
    }

    @Override
    public Balance getSource() {
        return (Balance) super.getSource();
    }

}
