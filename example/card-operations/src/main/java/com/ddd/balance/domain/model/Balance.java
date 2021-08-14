package com.ddd.balance.domain.model;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.stream.Stream;

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
    Long customerId,

    @Column("LAST_UPDATE")
    Timestamp lastUpdate,

    @Column("WITHDRAW_LIMIT")
    BigDecimal withdrawLimit
) {

    private static Logger logger = LoggerFactory.getLogger(Balance.class);

    //TODO Maybe, it is necessary to move that part to another class
    //Add invariants rules
    private Boolean validateRules(BigDecimal amount) {

        //Rule 1: Enough Money to Withdraw
        BiPredicate<Balance, BigDecimal> rule_enough_money =
                (currentBalance, quantity) -> currentBalance.balance().compareTo(quantity) >= 0;

        //Rule 2: Only one withdraw in last hour
        BiPredicate<Balance, BigDecimal> rule_only_one_withdraw_in_the_same_hour =
                (currentBalance, quantity) -> {

                    //No withdraw previously
                    if(Objects.isNull(currentBalance.lastUpdate)) {
                        logger.trace("No withdraw previously");
                        return true;
                    }

                    //Multiple withdraw previously
                    Timestamp now = Timestamp.from(Instant.now());
                    if(currentBalance.lastUpdate.before(now)) {
                        logger.trace("At least, one withdraw previously");

                        long diffHours = ChronoUnit.HOURS.between(
                                now.toLocalDateTime(), currentBalance.lastUpdate.toLocalDateTime());

                        if (diffHours >= 1) {
                            return true;
                        }
                    }

                    return false;
                };

        //Rule 3: Review limit
        BiPredicate<Balance, BigDecimal> rule_limit =
                (currentBalance, quantity) -> {
                    if (Objects.isNull(currentBalance.withdrawLimit)) {
                        return true;
                    }

                    return currentBalance.withdrawLimit().compareTo(quantity) != -1;
                };

        //Execute all rules
        record ruleTuple(Balance balance, BigDecimal amount) {}
        Long count = Stream.of(new ruleTuple(this, amount))
                .filter(t -> rule_enough_money.test(t.balance(), t.amount))
                .filter(t -> rule_only_one_withdraw_in_the_same_hour.test(t.balance(), t.amount))
                .filter(t -> rule_limit.test(t.balance, amount))
                .count();

        if(count == 1) {
            return true;
        }
        return false;
    }

    //TODO: Optional or Either(vavr)
    public Optional<Balance> withdraw(BigDecimal amount) {

        if (validateRules(amount)) {
            return Optional.of(new Balance(
                    this.balanceId(),
                    this.balance().subtract(amount),
                    this.customerId(),
                    Timestamp.from(Instant.now()),
                    this.withdrawLimit));
        }

        return Optional.empty();
    }
}
