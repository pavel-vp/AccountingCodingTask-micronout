package com.micronaut.accounting.service;


import com.micronaut.accounting.dao.DaoMemoryImpl;
import com.micronaut.accounting.model.StatusRecord;
import io.micronaut.scheduling.annotation.Scheduled;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.math.BigDecimal;

@Singleton
public class TransferService {

    @Inject
    DaoMemoryImpl dao;

    public Long deposit(String account, BigDecimal amount) {
        return dao.deposit(account, amount);
    }

    public BigDecimal getAmount(String account) {
        return dao.getAmount(account);
    }

    public Long transfer(String accountFrom, String accountTo, BigDecimal amount) {
        return dao.transfer(accountFrom, accountTo, amount);
    }

    public StatusRecord getStatus(Long operationId) {
        return dao.getStatus(operationId);
    }

    @Scheduled(fixedDelay = "1ms")
    public void doTransferTasks() {
        dao.proceedNextTask();
    }

}
