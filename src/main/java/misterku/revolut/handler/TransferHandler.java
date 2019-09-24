package misterku.revolut.handler;

import com.google.gson.Gson;
import misterku.revolut.model.TransferRequest;
import misterku.revolut.model.exception.BadRequestException;
import misterku.revolut.model.service.TransferResult;
import misterku.revolut.service.AccountService;
import spark.Request;
import spark.Response;

public class TransferHandler {
    private final AccountService accountService;
    private final Gson gson;

    public TransferHandler(AccountService accountService, Gson gson) {
        this.accountService = accountService;
        this.gson = gson;
    }

    public String transfer(Request request, Response response) {
        TransferRequest r = gson.fromJson(request.body(), TransferRequest.class);
        if (r.getSourceId() == null) {
            throw new BadRequestException("sourceId is null");
        }
        if (r.getDestinationId() == null) {
            throw new BadRequestException("destinationId is null");
        }
        if (r.getAmount() == null) {
            throw new BadRequestException("amount is null");
        }
        TransferResult result = accountService.transfer(r.getSourceId(), r.getDestinationId(), r.getAmount());
        if (result.isSuccess()) {
            response.status(200);
            return gson.toJson(result);
        } else {
            return gson.toJson(result);
        }
    }
}
