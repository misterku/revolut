package misterku.revolut;

import com.google.gson.Gson;
import misterku.revolut.handler.AccountHandler;
import misterku.revolut.handler.ErrorHandler;
import misterku.revolut.handler.TransferHandler;
import misterku.revolut.model.exception.BadRequestException;
import misterku.revolut.model.exception.NotFoundException;
import misterku.revolut.service.AccountService;

import static spark.Spark.*;

public class Main {

    public static void main(final String[] args) {
        final Gson gson = new Gson();
        final AccountService accountService = new AccountService();

        final AccountHandler accountHandler = new AccountHandler(accountService, gson);
        final TransferHandler transferHandler = new TransferHandler(accountService, gson);
        final ErrorHandler errorHandler = new ErrorHandler(gson);

        exception(NotFoundException.class, errorHandler::notFound);
        exception(BadRequestException.class, errorHandler::badRequest);
        exception(Exception.class, errorHandler::defaultError);

        post("/accounts", accountHandler::createNewAccount);
        get("/accounts/:id", accountHandler::getAccountBalance);
        post("/transfer", transferHandler::transfer);


    }
}
