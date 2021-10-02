package com.ddd.balance.domain.service;

import com.ddd.balance.domain.service.FailedOperationResult.FailureReason;
import java.util.UUID;

public abstract class BalanceOperationResult {

   public static FailedOperationResult failure(FailureReason reason) {
      return new FailedOperationResult(reason);
   }

   public static SuccessfulOperationResult success(UUID balanceUUID) {
      return new SuccessfulOperationResult(balanceUUID);
   }

   public abstract boolean isSuccess();

   public final boolean isFailure() {
      return !isSuccess();
   }

}
