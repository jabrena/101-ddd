package com.ddd.balance.domain.service;

public class FailedOperationResult extends BalanceOperationResult {

   private final FailureReason reason;

   public FailedOperationResult(FailureReason reason) {
      this.reason = reason;
   }

   @Override
   public boolean isSuccess() {
      return false;
   }

   public FailureReason getReason() {
      return reason;
   }

   public enum FailureReason {
      BALANCE_DOES_NOT_EXIST,
      BALANCE_LIMIT_ALREADY_DEFINED,
      COULD_NOT_UPDATE_BALANCE,
      COULD_NOT_WITHDRAW_FROM_BALANCE,
      WITHDRAWAL_FAILED_DUE_TO_INSUFFICIENT_BALANCE,
      WITHDRAWAL_FAILED_DUE_TO_RECENT_WITHDRAWAL,
      WITHDRAWAL_FAILED_DUE_TO_LIMIT_NOT_DEFINE,
      UNKNOWN_FAILURE_REASON
   }

}
