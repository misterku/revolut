package misterku.revolut.model;

import java.math.BigDecimal;

public class AccountBalanceResponse {
    private final Integer accountId;
    private final BigDecimal balance;

    public AccountBalanceResponse(Integer id, BigDecimal balance) {
        this.accountId = id;
        this.balance = balance;
    }

    public Integer getAccountId() {
        return accountId;
    }

    public BigDecimal getBalance() {
        return balance;
    }
}
