package com.ddd.balance.infrastructure.rest;

public record BalanceOperationSuccessResponse(
    String balanceId
) implements BalanceOperationResponse {

}
