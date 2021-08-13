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
    BigDecimal BALANCE,

    @Column("ID_CUSTOMER")
    Long customerId
) {}