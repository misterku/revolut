package misterku.revolut.handler;

import misterku.revolut.model.Account;
import misterku.revolut.model.exception.AccountNotFoundException;
import misterku.revolut.model.exception.BadRequestException;
import misterku.revolut.model.http.TransferRequest;
import misterku.revolut.model.service.TransferResult;
import misterku.revolut.service.AccountService;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class TransferHandlerTest {
    private TransferHandler transferHandler;
    private AccountService accountService;

    @Before
    public void setUp() {
        accountService = mock(AccountService.class);
        transferHandler = new TransferHandler(accountService);
    }

    @Test
    public void testTransferSuccess() throws Exception {
        final var request = new TransferRequest();
        request.setSourceId(1);
        request.setDestinationId(2);
        request.setAmount(new BigDecimal("100"));

        doReturn(new TransferResult(true, ""))
                .when(accountService).transfer(eq(1), eq(2), any(BigDecimal.class));

        final var result = transferHandler.transfer(request);
        assertTrue(result.success());

        doReturn(new Account(1, new BigDecimal("0")))
                .when(accountService).getAccount(eq(1));
        doReturn(new Account(2, new BigDecimal("100")))
                .when(accountService).getAccount(eq(2));

        assertEquals(new BigDecimal("0"), accountService.getAccount(1).getAmount());
        assertEquals(new BigDecimal("100"), accountService.getAccount(2).getAmount());
    }

    @Test
    public void testTransferSameSrcAndDst() throws Exception {
        final var request = new TransferRequest();
        request.setSourceId(1);
        request.setDestinationId(1);
        request.setAmount(new BigDecimal("100"));

        doReturn(new TransferResult(false, ""))
                .when(accountService).transfer(eq(1), eq(1), any(BigDecimal.class));
        final var result = transferHandler.transfer(request);
        assertFalse(result.success());
    }


    @Test(expected = IllegalArgumentException.class)
    public void testTransferInvalidSrc() throws Exception {
        final var request = new TransferRequest();
        request.setSourceId(null);
        request.setDestinationId(2);
        request.setAmount(new BigDecimal("100"));
        doThrow(new IllegalArgumentException())
                .when(accountService).transfer(isNull(), any(), any());
        transferHandler.transfer(request);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testTransferInvalidDst() throws Exception {
        final var request = new TransferRequest();
        request.setSourceId(1);
        request.setDestinationId(null);
        request.setAmount(new BigDecimal("100"));
        doThrow(new IllegalArgumentException())
                .when(accountService).transfer(any(), isNull(), any());
        transferHandler.transfer(request);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testTransferNegativeSrc() throws Exception {
        final var request = new TransferRequest();
        request.setSourceId(-1);
        request.setDestinationId(2);
        request.setAmount(new BigDecimal("100"));
        doThrow(new IllegalArgumentException())
                .when(accountService).transfer(eq(-1), any(), any());
        transferHandler.transfer(request);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testTransferNegativeDst() throws Exception {
        final var request = new TransferRequest();
        request.setSourceId(1);
        request.setDestinationId(-2);
        request.setAmount(new BigDecimal("100"));
        doThrow(new IllegalArgumentException())
                .when(accountService).transfer(any(), eq(-2), any());
        transferHandler.transfer(request);
    }

    @Test(expected = AccountNotFoundException.class)
    public void testTransferMissingDst() throws Exception {
        final var request = new TransferRequest();
        request.setSourceId(1);
        request.setDestinationId(5);
        request.setAmount(new BigDecimal("100"));
        doThrow(new AccountNotFoundException(5))
                .when(accountService).transfer(any(), any(), any());
        transferHandler.transfer(request);
    }

    @Test(expected = AccountNotFoundException.class)
    public void testTransferMissingSrc() throws Exception {
        final var request = new TransferRequest();
        request.setSourceId(5);
        request.setDestinationId(2);
        request.setAmount(new BigDecimal("100"));
        doThrow(new AccountNotFoundException(5))
                .when(accountService).transfer(any(), any(), any());
        transferHandler.transfer(request);
    }

    @Test(expected = BadRequestException.class)
    public void testTransferNegativeAmount() throws Exception {
        final var request = new TransferRequest();
        request.setSourceId(1);
        request.setDestinationId(2);
        request.setAmount(new BigDecimal("-10"));
        transferHandler.transfer(request);
    }

    @Test
    public void testTransferNotEnoughMoney() throws Exception {
        final var request = new TransferRequest();
        request.setSourceId(1);
        request.setDestinationId(2);
        request.setAmount(new BigDecimal("500"));
        doReturn(new TransferResult(false, ""))
                .when(accountService).transfer(eq(1), eq(2), any(BigDecimal.class));
        final var result = transferHandler.transfer(request);
        assertFalse(result.success());
        doReturn(new Account(1, new BigDecimal("100")))
                .when(accountService).getAccount(eq(1));
        doReturn(new Account(2, new BigDecimal("0")))
                .when(accountService).getAccount(eq(2));
        assertEquals(new BigDecimal("100"), accountService.getAccount(1).getAmount());
        assertEquals(new BigDecimal("0"), accountService.getAccount(2).getAmount());
    }
}
