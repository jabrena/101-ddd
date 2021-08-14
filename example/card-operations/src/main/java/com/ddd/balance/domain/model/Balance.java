package com.ddd.balance.domain.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiPredicate;

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

        //Rule 1: Enough Money to Withdraw
        BiPredicate<BigDecimal, BigDecimal> rule_enough_money =
                (currentBalance, quantity) -> currentBalance.compareTo(quantity) >= 0;

        //TODO Only one withdraw in last hour
        //TODO Review limit

        return rule_enough_money.test(this.balance, amount);
    }

    //TODO: Optional or Either(vavr)
    public Optional<Balance> withdraw(BigDecimal amount) {

        if (validateRules(amount)) {
            return Optional.of(new Balance(
                    this.balanceId(),
                    this.balance().subtract(amount),
                    this.customerId()));
        }

        return Optional.empty();
    }
}
