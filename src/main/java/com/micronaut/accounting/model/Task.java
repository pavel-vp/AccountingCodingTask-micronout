package com.micronaut.accounting.model;

import java.math.BigDecimal;

public class Task {
    final private Long operationId;
    final private String accountFrom;
    final private String accountTo;
    final private BigDecimal amount;

    public Task(Long operationId, String accountFrom, String accountTo, BigDecimal amount) {
        this.operationId = operationId;
        this.accountFrom = accountFrom;
        this.accountTo = accountTo;
        this.amount = amount;
    }

    public Long getOperationId() {
        return operationId;
    }

    public String getAccountFrom() {
        return accountFrom;
    }

    public String getAccountTo() {
        return accountTo;
    }

    public BigDecimal getAmount() {
        return amount;
    }
}
