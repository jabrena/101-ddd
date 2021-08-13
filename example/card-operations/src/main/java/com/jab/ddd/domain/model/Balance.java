package com.jab.ddd.domain.model;

import java.math.BigDecimal;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("BALANCE")
public record Balance (

    @Id 
    @Column("ID_BALANCE")
    Long balanceId, 

    @Column("BALANCE")
    BigDecimal balance,

    @Column("ID_CUSTOMER")
    Long customerId

) {

    public Balance witddraw(BigDecimal amount) {
        return new Balance(
            this.balanceId(),
            this.balance().subtract(amount),
            this.customerId());
    }
}