package misterku.revolut.handler;

import com.google.gson.Gson;
import misterku.revolut.model.Account;
import misterku.revolut.model.http.NewAccountRequest;
import misterku.revolut.model.exception.BadRequestException;
import misterku.revolut.service.AccountService;
import spark.Request;
import spark.Response;

import java.math.BigDecimal;

public class AccountHandler {
    private final AccountService accountService;
    private final Gson gson;

    public AccountHandler(AccountService accountService, Gson gson) {
        this.accountService = accountService;
        this.gson = gson;
    }

    public Account createNewAccount(NewAccountRequest request) {
        if (request.getAccountId() == null) {
            throw new BadRequestException("accountId is null");
        }
        if (request.getAccountId() < 0) {
            throw new BadRequestException("accountId is negative");
        }
        if (request.getAmount() == null) {
            throw new BadRequestException("amount is null");
        }
        if (request.getAmount().compareTo(BigDecimal.ZERO) < 0) {
            throw new BadRequestException("amount is negative");
        }
        return accountService.createNewAccount(request.getAccountId(), request.getAmount());
    }

    public Account getAccount(String id) {
        try {
            Integer accountId = Integer.parseInt(id);
            return accountService.getAccount(accountId);
        } catch (NumberFormatException e) {
            throw new BadRequestException("invalid account id");
        }
    }
}
