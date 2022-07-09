package misterku.revolut.handler;

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
        if (transferRequest.amountIsNull()) {
            throw new BadRequestException("amount is null");
        }
        if (transferRequest.amountIsNegative()) {
            throw new BadRequestException("amount < 0");
        }
        return accountService.transfer(transferRequest.getSourceId(), transferRequest.getDestinationId(), transferRequest.getAmount());
    }
}
