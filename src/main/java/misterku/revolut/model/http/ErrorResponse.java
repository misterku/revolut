package misterku.revolut.model.http;

public record ErrorResponse(String status, String error) {
    public ErrorResponse(String error) {
        this("error", error);
    }

}
