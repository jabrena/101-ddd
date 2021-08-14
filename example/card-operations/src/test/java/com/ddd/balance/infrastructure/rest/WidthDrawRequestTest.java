package com.ddd.balance.infrastructure.rest;

import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import java.math.BigDecimal;
import java.util.Set;

import static org.assertj.core.api.BDDAssertions.then;

/**
 * Applying technique The Right Bicep
 *
 * Right – Are the results right ? Are the results correct?
 * B – are all the boundary conditions correct? Are all the boundary conditions correct?
 * I – can you check the inverse relationships? Can you check the inverse relationships?
 * C – can you cross-check results using other means? Could you cross-check results using other means?
 * E – can you force error conditions to happen? Is it possible to force error conditions to occur?
 * P – are performance characteristics within bounds? Are performance requirements met?
 *
 * Boundary Conditions
 *
 * Completely forged or inconsistent input data
 * Malformed data
 * Null or incomplete value
 * Some values that are far from expected reasonable values
 * A sequence with no duplicate values is required, but a sequence with duplicate values is passed in
 * An ordered Xu Liu is required, but an incoming sequence is not required
 * The order in which the events arrived was wrong, or happened to be inconsistent with the expected order
 */
public class WidthDrawRequestTest {

    @Test
    public void given_request_when_happy_path_then_Ok() {

        //Given
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        //When
        WidthDrawRequest loginRequest = new WidthDrawRequest(1L, new BigDecimal("100.00"));
        Set<ConstraintViolation<WidthDrawRequest>> violations = validator.validate(loginRequest);

        //Then
        then(violations.size()).isEqualTo(0);
    }

    @Test
    public void given_request_when_idcustomer_is_zero_then_Ko() {

        //Given
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        //When
        WidthDrawRequest loginRequest = new WidthDrawRequest(0L, new BigDecimal("100.00"));
        Set<ConstraintViolation<WidthDrawRequest>> violations = validator.validate(loginRequest);

        //Then
        then(violations.size()).isEqualTo(1);
    }

    @Test
    public void given_request_when_idcustomer_is_negative_then_Ko() {

        //Given
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        //When
        WidthDrawRequest loginRequest = new WidthDrawRequest(-1L, new BigDecimal("100.00"));
        Set<ConstraintViolation<WidthDrawRequest>> violations = validator.validate(loginRequest);

        //Then
        then(violations.size()).isEqualTo(1);
    }

    @Test
    public void given_request_when_idcustomer_is_in_range_then_Ok() {

        //Given
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        //When
        WidthDrawRequest loginRequest = new WidthDrawRequest(Long.MAX_VALUE, new BigDecimal("100.00"));
        Set<ConstraintViolation<WidthDrawRequest>> violations = validator.validate(loginRequest);

        //Then
        then(violations.size()).isEqualTo(0);
    }

    @Test
    public void given_request_when_amount_is_positive_then_Ok() {

        //Given
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        //When
        WidthDrawRequest loginRequest = new WidthDrawRequest(1L, new BigDecimal("1.00"));
        Set<ConstraintViolation<WidthDrawRequest>> violations = validator.validate(loginRequest);

        //Then
        then(violations.size()).isEqualTo(0);
    }

    @Test
    public void given_request_when_amount_has_many_decimals_then_Ko() {

        //Given
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        //When
        WidthDrawRequest loginRequest = new WidthDrawRequest(1L, new BigDecimal("1.000"));
        Set<ConstraintViolation<WidthDrawRequest>> violations = validator.validate(loginRequest);

        //Then
        then(violations.size()).isEqualTo(1);
    }

    @Test
    public void given_request_when_amount_is_negative_then_Ko() {

        //Given
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        //When
        WidthDrawRequest loginRequest = new WidthDrawRequest(1L, new BigDecimal("-1.00"));
        Set<ConstraintViolation<WidthDrawRequest>> violations = validator.validate(loginRequest);

        //Then
        then(violations.size()).isEqualTo(1);
    }
}
