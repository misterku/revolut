package misterku.revolut.handler;

import com.google.gson.Gson;
import misterku.revolut.model.AccountBalanceResponse;
import misterku.revolut.model.NewAccountRequest;
import misterku.revolut.model.NewAccountResponse;
import misterku.revolut.model.exception.BadRequestException;
import misterku.revolut.model.exception.NotFoundException;
import misterku.revolut.model.TransferRequest;
import misterku.revolut.model.service.TransferResult;
import misterku.revolut.service.AccountService;
import spark.Request;
import spark.Response;

import java.math.BigDecimal;

import static spark.Spark.halt;

public class AccountHandler {
    private final AccountService accountService;
    private final Gson gson;

    public AccountHandler(AccountService accountService, Gson gson) {
        this.accountService = accountService;
        this.gson = gson;
    }

    public String createNewAccount(Request request, Response response) {
        NewAccountRequest r = gson.fromJson(request.body(), NewAccountRequest.class);
        if (r.getAccountId() == null) {
            throw new BadRequestException("accountId is null");
        }
        if (r.getAccountId() < 0) {
            throw new BadRequestException("accountId is negative");
        }
        if (r.getAmount() == null) {
            throw new BadRequestException("amount is null");
        }
        if (r.getAmount().compareTo(BigDecimal.ZERO) < 0) {
            throw new BadRequestException("amount is negative");
        }
        accountService.createNewAccount(r.getAccountId(), r.getAmount());
        NewAccountResponse resp = new NewAccountResponse(r.getAccountId(), r.getAmount());
        return gson.toJson(resp);
    }

    public String getAccountBalance(Request request, Response response) {
        String id = request.params(":id");
        try {
            Integer accountId = Integer.parseInt(id);
            if (accountId < 0) {
                throw new BadRequestException("accountId is negative");
            }
            final BigDecimal balance = accountService.getBalance(accountId);
            return gson.toJson(new AccountBalanceResponse(accountId, balance));
        } catch (NumberFormatException e) {
            throw new BadRequestException("invalid account id");
        }
    }




}
