package com.ddd.balance.domain.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

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

        //Enough Money to Widhdraw
        BiPredicate<BigDecimal, BigDecimal> rule_enough_money =
                (currentBalance, quantity) -> currentBalance.compareTo(quantity) >= 0;

        //TODO No more widthdraw in last hour
        //TODO Review limit

        return rule_enough_money.test(this.balance, amount);
    }

    public Optional<Balance> witdhdraw(BigDecimal amount) {

        if (validateRules(amount)) {
            return Optional.of(new Balance(
                    this.balanceId(),
                    this.balance().subtract(amount),
                    this.customerId()));
        }

        return Optional.empty();
    }
}
