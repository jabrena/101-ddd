package com.ddd.balance.domain.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("CUSTOMER")
public record Customer (

    @Id 
    @Column("ID_CUSTOMER")
    Long id,

    @Column("FIRST_NAME")
    String firstName,

    @Column("LAST_NAME")
    String lastName,

    @Column("COUNTRY_IDENTIFIER")
    String identifier
) { }
