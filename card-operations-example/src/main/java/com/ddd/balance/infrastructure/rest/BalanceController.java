package com.ddd.balance.infrastructure.rest;

import com.ddd.balance.domain.model.Balance;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.ddd.balance.application.BalanceService;

import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.Valid;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Optional;

@RestController
@RequestMapping(
        path = "/api",
        //consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE)
public class BalanceController {

    @Autowired
    private BalanceService balanceService;

    @PostMapping("/withdraw")
    public ResponseEntity<WithdrawResponse> withdraw(
            @Valid @RequestBody WithdrawRequest withdrawRequest,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(new WithdrawResponse(false));
        }

        Optional<Balance> witdhdraw = balanceService.witdhdraw(withdrawRequest.idCustomer(), withdrawRequest.amount());

        //TODO Refactor the usage of Optional
        if(witdhdraw.isPresent()) {
            return ResponseEntity.ok().body(new WithdrawResponse(true));
        } else {
            return ResponseEntity.ok().body(new WithdrawResponse(false));
        }
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<WithdrawResponse> handleException() {
        return ResponseEntity.ok().body(new WithdrawResponse(false));
    }

    @GetMapping("/balance/{id}")
    public ResponseEntity<BalanceResponse> balance(@PathVariable long id) {

        Optional<Balance> balance = balanceService.balance(id);

        //TODO Refactor the usage of Optional
        if(balance.isPresent()) {
            return ResponseEntity.ok().body(new BalanceResponse(balance.get().balanceId(), balance.get().balance()));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/withdrawlimit")
    public ResponseEntity<WithdrawResponse> withdrawlimit(
            @Valid @RequestBody WithdrawLimitRequest withdrawRequest,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(new WithdrawResponse(false));
        }

        Optional<Balance> witdhdrawlimit = balanceService.witdhdrawLimit(withdrawRequest.idCustomer(), withdrawRequest.amount());

        //TODO Refactor the usage of Optional
        if(witdhdrawlimit.isPresent()) {
            return ResponseEntity.ok().body(new WithdrawResponse(true));
        } else {
            return ResponseEntity.ok().body(new WithdrawResponse(false));
        }
    }

    @PostMapping("/repay")
    public ResponseEntity<RepayResponse> repay(
            @Valid @RequestBody RepayRequest repayRequest,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(new RepayResponse(false));
        }

        Optional<Balance> witdhdrawlimit = balanceService.repay(repayRequest.idCustomer(), repayRequest.amount());

        //TODO Refactor the usage of Optional
        if(witdhdrawlimit.isPresent()) {
            return ResponseEntity.ok().body(new RepayResponse(true));
        } else {
            return ResponseEntity.ok().body(new RepayResponse(false));
        }


    }

}
