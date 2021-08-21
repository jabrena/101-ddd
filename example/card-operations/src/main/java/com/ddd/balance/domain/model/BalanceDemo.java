package com.ddd.balance.domain.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.domain.AbstractAggregateRoot;
import org.springframework.data.relational.core.mapping.Column;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class BalanceDemo extends AbstractAggregateRoot<BalanceDemo> {

    @Id
    @Column("ID_BALANCE")
    Long balanceId;

    @Column("BALANCE")
    BigDecimal balance;

    @Column("ID_CUSTOMER")
    Long customerId;

    @Column("LAST_UPDATE")
    Timestamp lastUpdate;

    @Column("WITHDRAW_LIMIT")
    BigDecimal withdrawLimit;
}
