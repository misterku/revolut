package misterku.revolut.handler;

import misterku.revolut.model.Account;
import misterku.revolut.model.exception.BadRequestException;
import misterku.revolut.model.http.NewAccountRequest;
import misterku.revolut.service.AccountService;

import java.math.BigDecimal;

public class AccountHandler {
    private final AccountService accountService;

    public AccountHandler(AccountService accountService) {
        this.accountService = accountService;
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
