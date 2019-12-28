package com.micronaut.accounting;

import com.micronaut.accounting.dao.Dao;
import com.micronaut.accounting.dao.DaoMemoryImpl;
import com.micronaut.accounting.model.StatusRecord;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

public class AccountTest {

    private Dao dao;

    @Before
    public void setUp() {
        dao = new DaoMemoryImpl();
        new Timer()
                .scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                dao.proceedNextTask();
            }
        }, 0, 1);
    }

    @Test
    public void concurrent_transfer_test() {
        int count = 10000;
        double initAmount = 100;
        Queue<Long> tasks = new ConcurrentLinkedQueue<>();
        for (int i = 1; i<=count; i++) {
            final String account = String.valueOf(i);
            new Thread(() -> tasks.add(dao.deposit(account, BigDecimal.valueOf(initAmount)))).start();
        }

        for (int i = 1; i<=count * 10; i++) {
            new Thread(() -> {
                String accountFrom = String.valueOf((int)(1 + Math.random()*count));
                String accountTo = String.valueOf((int)(1 + Math.random()*count));
                tasks.add(dao.transfer(accountFrom, accountTo, BigDecimal.valueOf((int)(1 + Math.random() * initAmount))));
            }).start();
        }

        boolean isAllTaskDone = false;
        while(!isAllTaskDone) {
            isAllTaskDone = true;
            for (Long taskId : tasks) {
                StatusRecord status = dao.getStatus(taskId);
                if (status == StatusRecord.INPROGRESS) {
                    isAllTaskDone = false;
                }
            }
        }
        BigDecimal total = BigDecimal.ZERO;
        for (int i = 1; i <= count; i++) {
            BigDecimal amount = dao.getAmount(String.valueOf(i));
            total = total.add(amount);
        }
        Assert.assertEquals(total.compareTo(BigDecimal.valueOf(count * initAmount)), 0);


    }
}
