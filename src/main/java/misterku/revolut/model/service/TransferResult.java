package misterku.revolut.model.service;

public class TransferResult {
    private final boolean success;
    private final String message;

    public TransferResult(final boolean success, final String message) {
        this.success = success;
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }
}
