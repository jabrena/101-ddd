package com.ddd.balance.infrastructure.rest;

import com.ddd.balance.domain.service.BalanceOperationResult;
import com.ddd.balance.domain.service.BalanceService;
import com.ddd.balance.domain.service.FailedOperationResult;
import com.ddd.balance.domain.service.SuccessfulOperationResult;
import java.util.Optional;
import java.util.UUID;
import javax.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api")
public class BalanceController {

   private final BalanceService balanceService;

   public BalanceController(BalanceService balanceService) {
      this.balanceService = balanceService;
   }

   @PostMapping(value = "/balance", consumes = MediaType.APPLICATION_JSON_VALUE,
       produces = MediaType.APPLICATION_JSON_VALUE)
   public ResponseEntity<BalanceOperationResponse> createBalance(
       @Valid @RequestBody CreateBalanceRequest createBalanceRequest,
       BindingResult bindingResult) {

      if (bindingResult.hasErrors()) {
         return mapBindingResultErrors(bindingResult);
      }

      Optional<UUID> customerId = parseUUID(createBalanceRequest.idCustomer());
      if (customerId.isEmpty()) {
         return mapBadUuid("customerID");
      }

      BalanceOperationResult operationResult = balanceService.createBalance(customerId.get(),
          createBalanceRequest.amount());

      return operationResult.isSuccess() ?
          mapSuccessfulOperation((SuccessfulOperationResult) operationResult) :
          mapFailedOperation((FailedOperationResult) operationResult);
   }

   @GetMapping(value = "/balance/{balanceId}",
       produces = MediaType.APPLICATION_JSON_VALUE)
   public ResponseEntity<BalanceOperationResponse> getBalance(
       @PathVariable("balanceId") String strBalanceId) {

      Optional<UUID> balanceId = parseUUID(strBalanceId);
      if (balanceId.isEmpty()) {
         return mapBadUuid("balanceId");
      }

      return balanceService.getBalance(balanceId.get())
          .map(balance -> ResponseEntity.ok().body(
              (BalanceOperationResponse) new GetBalanceResponse(balance.getId().id().toString(), balance.getCustomerId().toString(),
                  balance.getBalance(), balance.getBalanceLimit())))
          .orElse(ResponseEntity.notFound().build());
   }

   private Optional<UUID> parseUUID(String value) {
      try {
         return Optional.of(UUID.fromString(value));
      } catch (IllegalArgumentException iae) {
         return Optional.empty();
      }
   }

   @PostMapping(value = "/balance/{balanceId}/defineBalanceLimit", consumes = MediaType.APPLICATION_JSON_VALUE,
       produces = MediaType.APPLICATION_JSON_VALUE)
   public ResponseEntity<BalanceOperationResponse> defineBalanceLimit(
       @PathVariable("balanceId") String strBalanceId,
       @Valid @RequestBody DefineBalanceLimitRequest request,
       BindingResult bindingResult) {

      if (bindingResult.hasErrors()) {
         return mapBindingResultErrors(bindingResult);
      }

      Optional<UUID> balanceId = parseUUID(strBalanceId);
      if (balanceId.isEmpty()) {
         return ResponseEntity.badRequest().body(
             new BalanceOperationFailureResponse("BAD_INPUT",
                 "Invalid balanceId parameter. Must be a UUID."));
      }

      BalanceOperationResult operationResult = balanceService.defineLimit(balanceId.get(),
          request.amount());
      return operationResult.isSuccess() ?
          mapSuccessfulOperation((SuccessfulOperationResult) operationResult) :
          mapFailedOperation((FailedOperationResult) operationResult);
   }

   private ResponseEntity<BalanceOperationResponse> mapSuccessfulOperation(
       SuccessfulOperationResult operationResult) {
      return ResponseEntity
          .ok(new BalanceOperationSuccessResponse(operationResult.getBalanceUUID().toString()));
   }

   @PostMapping(value = "/balance/{balanceId}/withdrawMoney", consumes = MediaType.APPLICATION_JSON_VALUE,
       produces = MediaType.APPLICATION_JSON_VALUE)
   public ResponseEntity<BalanceOperationResponse> withdrawMoney(
       @PathVariable("balanceId") String strBalanceId,
       @Valid @RequestBody WithdrawMoneyRequest request,
       BindingResult bindingResult) {

      if (bindingResult.hasErrors()) {
         return mapBindingResultErrors(bindingResult);
      }

      Optional<UUID> balanceId = parseUUID(strBalanceId);
      if (balanceId.isEmpty()) {
         return mapBadUuid("balanceId");
      }

      BalanceOperationResult operationResult = balanceService.withdraw(balanceId.get(),
          request.amount());
      return operationResult.isSuccess() ?
          mapSuccessfulOperation((SuccessfulOperationResult) operationResult) :
          mapFailedOperation((FailedOperationResult) operationResult);
   }

   @PostMapping(value = "/balance/{balanceId}/repayMoney", consumes = MediaType.APPLICATION_JSON_VALUE,
       produces = MediaType.APPLICATION_JSON_VALUE)
   public ResponseEntity<BalanceOperationResponse> withdrawMoney(
       @PathVariable("balanceId") String strBalanceId,
       @Valid @RequestBody RepayMoneyRequest request,
       BindingResult bindingResult) {

      if (bindingResult.hasErrors()) {
         return mapBindingResultErrors(bindingResult);
      }

      Optional<UUID> balanceId = parseUUID(strBalanceId);
      if (balanceId.isEmpty()) {
         return mapBadUuid("balanceId");
      }

      BalanceOperationResult operationResult = balanceService.repay(balanceId.get(),
          request.amount());
      return operationResult.isSuccess() ?
          mapSuccessfulOperation((SuccessfulOperationResult) operationResult) :
          mapFailedOperation((FailedOperationResult) operationResult);
   }


   private ResponseEntity<BalanceOperationResponse> mapBadUuid(String field) {
      return ResponseEntity.badRequest()
          .body(new BalanceOperationFailureResponse("BAD_INPUT",
              "Invalid %s parameter. Must be a UUID.".formatted(field)));
   }

   private ResponseEntity<BalanceOperationResponse> mapBindingResultErrors(
       BindingResult bindingResult) {
      return ResponseEntity.badRequest().body(
          new BalanceOperationFailureResponse("BAD_INPUT_VALIDATION", bindingResult.toString()));
   }

   private ResponseEntity<BalanceOperationResponse> mapFailedOperation(
       FailedOperationResult operationResult) {
      return ResponseEntity
          .status(HttpStatus.BAD_REQUEST)
          .body(new BalanceOperationFailureResponse(operationResult.getReason().toString()));
   }
}
