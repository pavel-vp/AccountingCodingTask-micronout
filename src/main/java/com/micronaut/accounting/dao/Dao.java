package com.micronaut.accounting.dao;

import com.micronaut.accounting.model.StatusRecord;

import java.math.BigDecimal;

public interface Dao {
    Long deposit(String account, BigDecimal amount);

    BigDecimal getAmount(String account);

    Long transfer(String accountFrom, String accountTo, BigDecimal amount);

    StatusRecord getStatus(Long operationId);

    void proceedNextTask();

}
