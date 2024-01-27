package misterku.revolut.web;

import io.javalin.Javalin;
import io.javalin.http.ContentType;
import misterku.revolut.handler.AccountHandler;
import misterku.revolut.handler.TransferHandler;
import misterku.revolut.model.exception.BadRequestException;
import misterku.revolut.model.exception.NotFoundException;
import misterku.revolut.model.http.ErrorResponse;
import misterku.revolut.model.http.NewAccountRequest;
import misterku.revolut.model.http.TransferRequest;
import misterku.revolut.service.AccountService;

public class Handlers {

    private final AccountHandler accountHandler;
    private final TransferHandler transferHandler;
    private final Javalin app;

    public Handlers() {
        this(8080);
    }

    public Handlers(int port) {
        final var accountService = new AccountService();
        accountHandler = new AccountHandler(accountService);
        transferHandler = new TransferHandler(accountService);

        app = Javalin.create();
        app.post("/accounts", ctx -> {
            final var request = ctx.bodyAsClass(NewAccountRequest.class);
            final var result = accountHandler.createNewAccount(request);
            ctx.status(200);
            ctx.contentType(ContentType.APPLICATION_JSON);
            ctx.json(result);
        });
        app.get("/accounts/{id}", ctx -> {
            ctx.status(200);
            ctx.contentType(ContentType.APPLICATION_JSON);
            ctx.json(accountHandler.getAccount(ctx.pathParam("id")));
        });
        app.post("/transfer", ctx -> {
            final var request = ctx.bodyAsClass(TransferRequest.class);
            final var result = transferHandler.transfer(request);
            ctx.status(200);
            ctx.contentType(ContentType.APPLICATION_JSON);
            ctx.json(result);
        });
        app.exception(NotFoundException.class, (e, ctx) -> {
            ctx.status(404);
            ctx.contentType(ContentType.APPLICATION_JSON);
            ctx.json(new ErrorResponse(e.getMessage()));
        });
        app.exception(BadRequestException.class, (e, ctx) -> {
            ctx.status(400);
            ctx.contentType(ContentType.APPLICATION_JSON);
            ctx.json(new ErrorResponse(e.getMessage()));
        });
        app.exception(Exception.class, (e, ctx) -> {
            ctx.status(500);
            ctx.contentType(ContentType.APPLICATION_JSON);
            ctx.json(new ErrorResponse(e.getMessage()));
        });
        app.start(port);

    }

    public void stop() {
        app.close();
    }
}
