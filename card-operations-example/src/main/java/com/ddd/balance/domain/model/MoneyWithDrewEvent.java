package com.ddd.balance.domain.model;

import org.springframework.context.ApplicationEvent;

public class MoneyWithDrewEvent extends ApplicationEvent {

    public MoneyWithDrewEvent(Balance source) {
        super(source);
    }

    @Override
    public Balance getSource() {
        return (Balance) super.getSource();
    }

}
