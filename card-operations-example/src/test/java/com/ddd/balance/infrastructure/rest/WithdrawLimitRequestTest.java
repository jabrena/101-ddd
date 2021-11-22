package com.ddd.balance.infrastructure.rest;

import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.math.BigDecimal;
import java.util.Set;

import static org.assertj.core.api.BDDAssertions.then;

public class WithdrawLimitRequestTest {

    @Test
    public void given_request_when_happy_path_then_Ok() {

        //Given
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        //When
        WithdrawLimitRequest withdrawLimitRequest = new WithdrawLimitRequest(1L, new BigDecimal("100.00"));
        Set<ConstraintViolation<WithdrawLimitRequest>> violations = validator.validate(withdrawLimitRequest);

        //Then
        then(violations.size()).isEqualTo(0);
    }

    @Test
    public void given_request_when_idcustomer_is_zero_then_Ko() {

        //Given
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        //When
        WithdrawLimitRequest withdrawLimitRequest = new WithdrawLimitRequest(0L, new BigDecimal("100.00"));
        Set<ConstraintViolation<WithdrawLimitRequest>> violations = validator.validate(withdrawLimitRequest);

        //Then
        then(violations.size()).isEqualTo(1);
    }

    @Test
    public void given_request_when_idcustomer_is_negative_then_Ko() {

        //Given
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        //When
        WithdrawLimitRequest withdrawLimitRequest = new WithdrawLimitRequest(-1L, new BigDecimal("100.00"));
        Set<ConstraintViolation<WithdrawLimitRequest>> violations = validator.validate(withdrawLimitRequest);

        //Then
        then(violations.size()).isEqualTo(1);
    }

    @Test
    public void given_request_when_idcustomer_is_in_range_then_Ok() {

        //Given
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        //When
        WithdrawLimitRequest withdrawLimitRequest = new WithdrawLimitRequest(Long.MAX_VALUE, new BigDecimal("100.00"));
        Set<ConstraintViolation<WithdrawLimitRequest>> violations = validator.validate(withdrawLimitRequest);

        //Then
        then(violations.size()).isEqualTo(0);
    }

    @Test
    public void given_request_when_amount_is_positive_then_Ok() {

        //Given
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        //When
        WithdrawLimitRequest withdrawLimitRequest = new WithdrawLimitRequest(1L, new BigDecimal("1.00"));
        Set<ConstraintViolation<WithdrawLimitRequest>> violations = validator.validate(withdrawLimitRequest);

        //Then
        then(violations.size()).isEqualTo(0);
    }

    @Test
    public void given_request_when_amount_has_many_decimals_then_Ko() {

        //Given
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        //When
        WithdrawLimitRequest withdrawLimitRequest = new WithdrawLimitRequest(1L, new BigDecimal("1.000"));
        Set<ConstraintViolation<WithdrawLimitRequest>> violations = validator.validate(withdrawLimitRequest);

        //Then
        then(violations.size()).isEqualTo(1);
    }

    @Test
    public void given_request_when_amount_is_negative_then_Ko() {

        //Given
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        //When
        WithdrawLimitRequest withdrawLimitRequest = new WithdrawLimitRequest(1L, new BigDecimal("-1.00"));
        Set<ConstraintViolation<WithdrawLimitRequest>> violations = validator.validate(withdrawLimitRequest);

        //Then
        then(violations.size()).isEqualTo(1);
    }
}
