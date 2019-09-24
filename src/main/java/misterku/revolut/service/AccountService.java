package misterku.revolut.service;

import misterku.revolut.model.exception.BadRequestException;
import misterku.revolut.model.exception.NotFoundException;
import misterku.revolut.model.service.TransferResult;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class AccountService {

    private final Map<Integer, BigDecimal> accounts;

    public AccountService() {
        accounts = new ConcurrentHashMap<>();
    }

    private boolean isAccountExists(final Integer id) {
        return accounts.containsKey(id);
    }

    public void createNewAccount(final Integer id, final BigDecimal amount) {
        if (accounts.putIfAbsent(id, amount) != null) {
            throw new BadRequestException("account is already exists");
        }
    }

    public BigDecimal getBalance(final Integer id) {
        if (isAccountExists(id)) {
            return accounts.get(id);
        } else {
            throw new NotFoundException("Account with id " + id + " is not found");
        }
    }

    //TODO this method should be in sync block
    public TransferResult transfer(final Integer srcId, final Integer dstId, final BigDecimal amount) {

        if (srcId < 0) {
            throw new BadRequestException("sourceId < 0");
        }
        if (dstId < 0) {
            throw new BadRequestException("destinationId < 0");
        }
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new BadRequestException("amount < 0");
        }
        if (!isAccountExists(srcId)) {
            throw new BadRequestException("Account with id " + srcId + " is not found");
        }
        if (!isAccountExists(dstId)) {
            throw new BadRequestException("Account with id " + dstId + " is not found");
        }
        if (accounts.get(srcId).compareTo(amount) < 0) {
            return new TransferResult(false);
        }
        accounts.put(srcId, accounts.get(srcId).subtract(amount));
        accounts.put(dstId, accounts.get(dstId).add(amount));
        return new TransferResult(true);
    }
}
