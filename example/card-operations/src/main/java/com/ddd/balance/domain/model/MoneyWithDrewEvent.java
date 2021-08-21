package com.ddd.balance.domain.model;

import org.springframework.context.ApplicationEvent;

public class MoneyWithDrewEvent extends ApplicationEvent {

    private static final long serialVersionUID = 5184509297655461859L;

    public MoneyWithDrewEvent(Balance source) {
        super(source);
    }

    @Override
    public Balance getSource() {
        return (Balance) super.getSource();
    }

}
