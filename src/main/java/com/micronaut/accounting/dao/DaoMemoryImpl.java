package com.micronaut.accounting.dao;

import com.micronaut.accounting.model.StatusRecord;
import com.micronaut.accounting.model.Task;

import javax.inject.Singleton;
import java.math.BigDecimal;
import java.util.Deque;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicLong;

@Singleton
public class DaoMemoryImpl implements Dao {

    private AtomicLong taskCounter = new AtomicLong();

    private Map<String, BigDecimal> accountRepo = new ConcurrentHashMap<>();

    private Deque<Task> taskList = new ConcurrentLinkedDeque<>();

    private Map<Long, StatusRecord> doneTaskMap = new ConcurrentHashMap<>();


    @Override
    public Long deposit(String account, BigDecimal amount) {
        Long id = taskCounter.incrementAndGet();
        taskList.add(new Task(id, null, account, amount));
        return id;
    }

    @Override
    public BigDecimal getAmount(String account) {
        BigDecimal amount = accountRepo.get(account);
        if (amount == null)
            return BigDecimal.ZERO;
        return amount;
    }

    @Override
    public Long transfer(String accountFrom, String accountTo, BigDecimal amount) {
        Long id = taskCounter.incrementAndGet();
        taskList.add(new Task(id, accountFrom, accountTo, amount));
        return id;
    }

    @Override
    public StatusRecord getStatus(Long operationId) {
        StatusRecord doneTask = doneTaskMap.get(operationId);
        if (doneTask == null) {
            return StatusRecord.INPROGRESS;
        }
        return doneTask;
    }

    @Override
    public void proceedNextTask() {
        Task task;
        while((task = taskList.pollFirst()) != null) {
            if (task.getAccountFrom() != null) {
                BigDecimal amountFrom = accountRepo.get(task.getAccountFrom());
                if (amountFrom == null || amountFrom.compareTo(task.getAmount()) < 0  ) {
                    doneTaskMap.put(task.getOperationId(), StatusRecord.ERROR);
                    return;
                }
                accountRepo.put(task.getAccountFrom(), new BigDecimal(amountFrom.subtract(task.getAmount()).doubleValue()));
            }
            BigDecimal amountTo = accountRepo.get(task.getAccountTo());
            if (amountTo == null) {
                accountRepo.put(task.getAccountTo(), new BigDecimal(task.getAmount().doubleValue()));
            } else {
                accountRepo.put(task.getAccountTo(), new BigDecimal(amountTo.add(task.getAmount()).doubleValue()));
            }
            doneTaskMap.put(task.getOperationId(), StatusRecord.SUCCESS);
        }
    }


}
