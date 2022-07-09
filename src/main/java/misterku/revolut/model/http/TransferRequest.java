package misterku.revolut.model.http;

import java.math.BigDecimal;

public class TransferRequest {
    private Integer sourceId;
    private Integer destinationId;
    private BigDecimal amount;

    public Integer getSourceId() {
        return sourceId;
    }

    public void setSourceId(Integer sourceId) {
        this.sourceId = sourceId;
    }

    public Integer getDestinationId() {
        return destinationId;
    }

    public void setDestinationId(Integer destinationId) {
        this.destinationId = destinationId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public boolean amountIsNull() {
        return this.amount == null;
    }

    public boolean amountIsNegative() {
        return this.amount.compareTo(BigDecimal.ZERO) < 0;
    }
}
