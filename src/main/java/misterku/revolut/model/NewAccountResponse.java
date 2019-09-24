package misterku.revolut.model;

import java.math.BigDecimal;

public class NewAccountResponse {
    private Integer accountId;
    private BigDecimal amount;

    public NewAccountResponse(Integer accountId, BigDecimal amount) {
        this.accountId = accountId;
        this.amount = amount;
    }

    public Integer getAccountId() {
        return accountId;
    }

    public void setAccountId(Integer accountId) {
        this.accountId = accountId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
