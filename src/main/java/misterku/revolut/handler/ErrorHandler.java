package misterku.revolut.handler;

import com.google.gson.Gson;
import misterku.revolut.model.http.ErrorResponse;
import spark.Request;
import spark.Response;

public class ErrorHandler {
    private final Gson gson;

    public ErrorHandler(Gson gson) {
        this.gson = gson;
    }

    public void notFound(Exception e, Request request, Response response) {
        response.status(404);
        response.type("application/json");
        response.body(gson.toJson(new ErrorResponse(e.getMessage())));
    }

    public void badRequest(Exception e, Request request, Response response) {
        response.status(400);
        response.type("application/json");
        response.body(gson.toJson(new ErrorResponse(e.getMessage())));
    }

    public void defaultError(Exception e, Request request, Response response) {
        response.status(500);
        response.type("application/json");
        response.body(gson.toJson(new ErrorResponse(e.getMessage())));
    }
}
