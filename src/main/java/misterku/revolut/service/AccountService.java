package misterku.revolut.service;

import misterku.revolut.model.Account;
import misterku.revolut.model.exception.AccountNotFoundException;
import misterku.revolut.model.exception.BadRequestException;
import misterku.revolut.model.exception.DuplicateAccountException;
import misterku.revolut.model.service.TransferResult;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;

public class AccountService {

    private final Map<Integer, Account> accounts;

    public AccountService() {
        accounts = new ConcurrentHashMap<>();
    }

    public Account createNewAccount(final Integer id, final BigDecimal amount) {
        Account account = new Account(id, amount);
        if (accounts.putIfAbsent(id, account) != null) {
            throw new DuplicateAccountException(id);
        } else {
            return account;
        }
    }

    public Account getAccount(final Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("accountId is null");
        }
        if (id < 0) {
            throw new IllegalArgumentException("accountId is negative");
        }
        Account account = accounts.get(id);
        if (account == null) {
            throw new AccountNotFoundException(id);
        } else {
            return account;
        }
    }

    private <T extends Comparable<T>, R> R doubleLockedExecution(T firstLock, T secondLock, Callable<R> action) throws Exception {
        if (firstLock.compareTo(secondLock) > 0) {
            return doubleLockedExecution(secondLock, firstLock, action);
        } else {
            synchronized (firstLock) {
                synchronized (secondLock) {
                    return action.call();
                }
            }
        }
    }

    public TransferResult transfer(final Integer srcId, final Integer dstId, final BigDecimal amount) throws Exception {
        Account source = getAccount(srcId);
        Account destination = getAccount(dstId);
        Integer lock1 = source.getAccountId();
        Integer lock2 = destination.getAccountId();

        if (Objects.equals(lock1, lock2)) {
            return new TransferResult(false, "Source and destination are the same");
        }
        return doubleLockedExecution(lock1, lock2, () -> {
            if (source.getAmount().compareTo(amount) < 0) {
                return new TransferResult(false, "Not enough money for transfer");
            }
            source.setAmount(source.getAmount().subtract(amount));
            destination.setAmount(destination.getAmount().add(amount));
            return new TransferResult(true, "Transfer was successful");
        });
    }
}
