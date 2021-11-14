package com.ddd.balance.infrastructure.rest;

import com.ddd.balance.domain.model.Balance;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import com.ddd.balance.application.BalanceService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.util.Optional;

@RestController
@RequestMapping(
        path = "/api",
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE)
public class BalanceController {

    @Autowired
    private BalanceService balanceService;

    @PostMapping("/withdraw")
    public Mono<WithdrawResponse> withdraw(
            @ModelAttribute Mono<WithdrawRequest>  withdrawRequest) {

        ResponseEntity<WithdrawResponse> ok = ResponseEntity.ok().body(new WithdrawResponse(true));
        ResponseEntity<WithdrawResponse> badRequest = ResponseEntity.badRequest().body(new WithdrawResponse(false));

        //balanceService.witdhdraw(withdrawRequest);


        return balanceService.witdhdraw(withdrawRequest)
                .map(x -> {
                    return new WithdrawResponse(false);
        }).single();
                //.defaultIfEmpty(badRequest);
    }

    @PostMapping("/withdrawlimit")
    public Mono<ServerResponse> withdrawlimit(
            @Valid @RequestBody WithdrawLimitRequest withdrawRequest, BindingResult bindingResult) {

        Mono<ServerResponse> ok = ServerResponse.ok().bodyValue(new WithdrawResponse(true));
        Mono<ServerResponse> badRequest = ServerResponse.badRequest().bodyValue(new WithdrawResponse(false));

        if (bindingResult.hasErrors()) {
            return ok;
        }

        return balanceService.witdhdrawLimit(withdrawRequest.idCustomer(), withdrawRequest.amount())
                .flatMap(x -> ok)
                .defaultIfEmpty((ServerResponse) badRequest);
    }

    @PostMapping("/repay")
    public Mono<ServerResponse> repay(
            @Valid @RequestBody RepayRequest repayRequest,
            BindingResult bindingResult) {

        Mono<ServerResponse> ok = ServerResponse.ok().bodyValue(new WithdrawResponse(true));
        Mono<ServerResponse> badRequest = ServerResponse.badRequest().bodyValue(new WithdrawResponse(false));

        if (bindingResult.hasErrors()) {
            return ok;
        }

        return balanceService.repay(repayRequest.idCustomer(), repayRequest.amount())
                .flatMap(x -> ok)
                .defaultIfEmpty((ServerResponse) badRequest);
    }

}
