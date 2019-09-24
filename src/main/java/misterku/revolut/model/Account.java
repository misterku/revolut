package misterku.revolut.model;

import java.math.BigDecimal;

public class Account {
    private final Integer accountId;
    private BigDecimal amount;

    public Account(Integer accountId, BigDecimal amount) {
        this.accountId = accountId;
        this.amount = amount;
    }

    public Integer getAccountId() {
        return accountId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
