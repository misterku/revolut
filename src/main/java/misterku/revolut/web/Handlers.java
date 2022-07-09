package misterku.revolut.web;

import com.google.gson.Gson;
import misterku.revolut.handler.AccountHandler;
import misterku.revolut.handler.ErrorHandler;
import misterku.revolut.handler.TransferHandler;
import misterku.revolut.model.exception.BadRequestException;
import misterku.revolut.model.exception.NotFoundException;
import misterku.revolut.model.http.NewAccountRequest;
import misterku.revolut.model.http.TransferRequest;
import misterku.revolut.service.AccountService;

import static spark.Spark.*;


public class Handlers {

    private static final String APPLICATION_JSON = "application/json";

    private final AccountHandler accountHandler;
    private final TransferHandler transferHandler;
    private final ErrorHandler errorHandler;
    private final Gson gson;

    public Handlers() {
        final var accountService = new AccountService();

        gson = new Gson();
        accountHandler = new AccountHandler(accountService);
        transferHandler = new TransferHandler(accountService);
        errorHandler = new ErrorHandler(gson);
    }

    public void init() {
        post("/accounts", (req, resp) -> {
            final var request = gson.fromJson(req.body(), NewAccountRequest.class);
            resp.type(APPLICATION_JSON);
            return accountHandler.createNewAccount(request);
        }, gson::toJson);
        get("/accounts/:id", (req, resp) -> {
            resp.type(APPLICATION_JSON);
            return accountHandler.getAccount(req.params(":id"));
        }, gson::toJson);
        post("/transfer", (req, resp) ->
                transferHandler.transfer(gson.fromJson(req.body(), TransferRequest.class)), gson::toJson);

        exception(NotFoundException.class, errorHandler::notFound);
        exception(BadRequestException.class, errorHandler::badRequest);
        exception(Exception.class, errorHandler::defaultError);
    }


}
