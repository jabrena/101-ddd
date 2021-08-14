package com.ddd.balance.infrastructure.rest;

import com.ddd.balance.domain.model.Balance;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;

import com.ddd.balance.application.BalanceService;

import org.springframework.beans.factory.annotation.Autowired;

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
    public ResponseEntity<WidthDrawResponse> withdraw(
            @Valid @RequestBody WidthDrawRequest widthDrawRequest,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(new WidthDrawResponse(false));
        }

        Optional<Balance> witdhdraw = balanceService.witdhdraw(widthDrawRequest.idCustomer(), widthDrawRequest.amount());

        //TODO Refactor the usage of Optional
        if(witdhdraw.isPresent()) {
            return ResponseEntity.ok().body(new WidthDrawResponse(true));
        } else {
            return ResponseEntity.ok().body(new WidthDrawResponse(false));
        }
    }

}
