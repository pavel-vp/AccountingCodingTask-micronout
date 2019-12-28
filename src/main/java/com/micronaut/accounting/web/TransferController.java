package com.micronaut.accounting.web;

import com.micronaut.accounting.service.TransferService;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.math.BigDecimal;

@Singleton
@Controller("/accounting")
public class TransferController {

    @Inject
    TransferService transferService;

    @Post("/deposit")
    @Produces(MediaType.TEXT_PLAIN)
    public Long deposit(@QueryValue("account") String account, @QueryValue("amount") BigDecimal amount) {
        return transferService.deposit(account, amount);
    }

    @Get("/amount")
    @Produces(MediaType.TEXT_PLAIN)
    public BigDecimal getAmount(@QueryValue("account") String account) {
        return transferService.getAmount(account);
    }

    @Post("/transfer")
    @Produces(MediaType.TEXT_PLAIN)
    public Long transfer(@QueryValue("accountfrom") String accountFrom, @QueryValue("accountto") String accountTo, @QueryValue("amount") BigDecimal amount) {
        return transferService.transfer(accountFrom, accountTo, amount);
    }

    @Get("/status")
    @Produces(MediaType.TEXT_PLAIN)
    public String status(@QueryValue("operationid") Long operationId) {
        return transferService.getStatus(operationId).name();
    }


}
