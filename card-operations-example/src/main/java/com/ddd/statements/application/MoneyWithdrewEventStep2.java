package com.ddd.statements.application;

import com.ddd.balance.domain.model.Balance;

import org.springframework.context.ApplicationEvent;

public class MoneyWithdrewEventStep2 extends ApplicationEvent {

    public MoneyWithdrewEventStep2(Balance source) {
        super(source);
    }

    @Override
    public Balance getSource() {
        return (Balance) super.getSource();
    }

}