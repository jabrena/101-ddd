package com.ddd.balance.infrastructure.rest;

public record BalanceOperationFailureResponse(
    String errorCode,
    String errorMessage
) implements BalanceOperationResponse {

   public BalanceOperationFailureResponse(String errorCode) {
      this(errorCode, null);
   }
}
