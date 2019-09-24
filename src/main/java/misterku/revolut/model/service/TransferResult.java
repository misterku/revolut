package misterku.revolut.model.service;

public class TransferResult {
    private final boolean success;

    public TransferResult(final boolean success) {
        this.success = success;
    }

    public boolean isSuccess() {
        return success;
    }
}
