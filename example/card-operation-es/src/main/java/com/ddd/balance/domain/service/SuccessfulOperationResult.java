package com.ddd.balance.domain.service;

import java.util.UUID;

public class SuccessfulOperationResult extends BalanceOperationResult {

   private final UUID balanceUUID;

   public SuccessfulOperationResult(UUID balanceUUID) {
      this.balanceUUID = balanceUUID;
   }

   public UUID getBalanceUUID() {
      return balanceUUID;
   }

   @Override
   public boolean isSuccess() {
      return true;
   }
}
