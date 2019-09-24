package misterku.revolut.model.http;

public class ErrorResponse {
    private String status;
    private String error;

    public ErrorResponse(String error) {
        this.error = error;
        this.status = "error";
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
