package com.micronaut.accounting;

import com.micronaut.accounting.model.StatusRecord;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.client.RxHttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.test.annotation.MicronautTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import javax.inject.Inject;

import static org.junit.jupiter.api.Assertions.*;

@MicronautTest
public class AccountingControllerTest {

    @Inject
    @Client("/")
    RxHttpClient client;

    @Test
    public void testDeposit() {
        HttpRequest<String> request = HttpRequest.POST("/accounting/deposit?account=1&amount=100", "");
        String body = client.toBlocking().retrieve(request);

        assertNotNull(body);
        assertDoesNotThrow(new Executable() {
            @Override
            public void execute() throws Throwable {
                Long operationId = Long.valueOf(body);
            }
        });
    }

    private String wait_for_operation_end(Long operationId) {
        while(true) {
            HttpRequest<String> requestGetStatus = HttpRequest.GET("/accounting/status?operationid="+operationId);
            String status = client.toBlocking().retrieve(requestGetStatus);
            if (!StatusRecord.INPROGRESS.name().equals(status)) {
                return status;
            }
        }
    }

    @Test
    public void testDeposit_and_getStatus_and_getAmount() {
        HttpRequest<String> requestDeposit = HttpRequest.POST("/accounting/deposit?account=2&amount=100", "");
        String operationId = client.toBlocking().retrieve(requestDeposit);
        assertNotNull(operationId);

        String status = wait_for_operation_end(Long.valueOf(operationId));
        assertEquals(status, StatusRecord.SUCCESS.name());

        HttpRequest<String> requestGetAmount = HttpRequest.GET("/accounting/amount?account=2");
        String amount = client.toBlocking().retrieve(requestGetAmount);
        assertEquals(amount,"100");
    }

    @Test
    public void testDeposit_and_getStatus_and_transfer_and_getAmount() {
        HttpRequest<String> requestDeposit = HttpRequest.POST("/accounting/deposit?account=3&amount=100", "");
        String operationId = client.toBlocking().retrieve(requestDeposit);
        assertNotNull(operationId);

        String status = wait_for_operation_end(Long.valueOf(operationId));
        assertEquals(status, StatusRecord.SUCCESS.name());

        HttpRequest<String> requestTransfer = HttpRequest.POST("/accounting/transfer?accountfrom=3&accountto=4&amount=50", "");
        String transferOperationId = client.toBlocking().retrieve(requestTransfer);
        assertNotNull(transferOperationId);

        String statusTransfer = wait_for_operation_end(Long.valueOf(transferOperationId));
        assertEquals(statusTransfer, StatusRecord.SUCCESS.name());

        HttpRequest<String> requestGetAmount1 = HttpRequest.GET("/accounting/amount?account=3");
        String amount1 = client.toBlocking().retrieve(requestGetAmount1);
        assertEquals(amount1,"50");

        HttpRequest<String> requestGetAmount2 = HttpRequest.GET("/accounting/amount?account=4");
        String amount2 = client.toBlocking().retrieve(requestGetAmount2);
        assertEquals(amount2,"50");
    }

    @Test
    public void testTransfer_notEnough_and_getCheck() {
        HttpRequest<String> requestTransfer = HttpRequest.POST("/accounting/transfer?accountfrom=99&accountto=2&amount=50", "");
        String transferOperationId = client.toBlocking().retrieve(requestTransfer);
        assertNotNull(transferOperationId);

        String statusTransfer = wait_for_operation_end(Long.valueOf(transferOperationId));
        assertEquals(statusTransfer, StatusRecord.ERROR.name());

    }
}
