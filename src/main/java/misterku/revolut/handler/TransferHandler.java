package misterku.revolut.handler;

import com.google.gson.Gson;
import misterku.revolut.model.exception.BadRequestException;
import misterku.revolut.model.http.TransferRequest;
import misterku.revolut.model.service.TransferResult;
import misterku.revolut.service.AccountService;

import java.math.BigDecimal;

public class TransferHandler {
    private final AccountService accountService;

    public TransferHandler(AccountService accountService) {
        this.accountService = accountService;
    }

    public TransferResult transfer(TransferRequest transferRequest) throws Exception {
        if (transferRequest.getAmount() == null) {
            throw new BadRequestException("amount is null");
        }
        if (transferRequest.getAmount().compareTo(BigDecimal.ZERO) < 0) {
            throw new BadRequestException("amount < 0");
        }
        return accountService.transfer(transferRequest.getSourceId(), transferRequest.getDestinationId(), transferRequest.getAmount());
    }
}
