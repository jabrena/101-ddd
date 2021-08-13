package com.ddd.balance.infrastructure.rest;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.stereotype.Service;

import com.ddd.balance.application.BalanceService;

import org.springframework.beans.factory.annotation.Autowired;

@RestController
public class BalanceController {
    
    @Autowired
    private BalanceService balanceService;

    @PostMapping("/withdraw")
    public void withdraw() {

    }

}
