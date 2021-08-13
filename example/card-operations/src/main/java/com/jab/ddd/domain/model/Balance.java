package com.jab.ddd.domain.model;

import java.math.BigDecimal;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    private static Logger logger = LoggerFactory.getLogger(Balance.class);

    //Add invariants rules
    private Boolean validateRules(BigDecimal amount) {

        if(this.balance.compareTo(amount) <= -1) {
            
        }

        return true;
    }

    public Optional<Balance> witdhdraw(BigDecimal amount) {

        return Optional.of(new Balance(
            this.balanceId(),
            this.balance().subtract(amount),
            this.customerId()));
    }
}