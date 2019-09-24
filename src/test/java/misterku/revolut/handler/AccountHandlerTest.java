package misterku.revolut.handler;

import com.google.gson.Gson;
import misterku.revolut.model.Account;
import misterku.revolut.model.exception.AccountNotFoundException;
import misterku.revolut.model.exception.BadRequestException;
import misterku.revolut.model.exception.DuplicateAccountException;
import misterku.revolut.model.http.NewAccountRequest;
import misterku.revolut.service.AccountService;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class AccountHandlerTest {

    private AccountHandler accountHandler;
    private AccountService accountService;

    @Before
    public void setUp() {
        accountService = mock(AccountService.class);
        accountHandler = new AccountHandler(accountService, new Gson());
    }

    @Test(expected = BadRequestException.class)
    public void createAccountWithNegativeAccountId() {
        NewAccountRequest request = new NewAccountRequest();
        request.setAccountId(-1);
        request.setAmount(new BigDecimal("100"));
        accountHandler.createNewAccount(request);
        verifyZeroInteractions(accountService);
    }

    @Test(expected = BadRequestException.class)
    public void createAccountWithInvalidAccountId() {
        NewAccountRequest request = new NewAccountRequest();
        request.setAccountId(null);
        request.setAmount(new BigDecimal("100"));
        accountHandler.createNewAccount(request);
        verifyZeroInteractions(accountService);
    }

    @Test(expected = BadRequestException.class)
    public void createAccountWithInvalidAmount() {
        NewAccountRequest request = new NewAccountRequest();
        request.setAccountId(5);
        request.setAmount(null);
        accountHandler.createNewAccount(request);
        verifyZeroInteractions(accountService);
    }

    @Test(expected = BadRequestException.class)
    public void createAccountWithNegativeAmount() {
        NewAccountRequest request = new NewAccountRequest();
        request.setAccountId(5);
        request.setAmount(new BigDecimal("-100"));
        accountHandler.createNewAccount(request);
        verifyZeroInteractions(accountService);
    }

    @Test
    public void createAccountSuccess() {
        NewAccountRequest request = new NewAccountRequest();
        request.setAccountId(10);
        request.setAmount(new BigDecimal("100"));
        doReturn(new Account(10, new BigDecimal("100")))
                .when(accountService).createNewAccount(eq(10), any(BigDecimal.class));
        Account account = accountHandler.createNewAccount(request);
        assertEquals(Integer.valueOf(10), account.getAccountId());
        assertEquals(new BigDecimal("100"), account.getAmount());
    }

    @Test(expected = DuplicateAccountException.class)
    public void createDuplicatedAccount() {
        NewAccountRequest request = new NewAccountRequest();
        request.setAccountId(5);
        request.setAmount(new BigDecimal("500"));
        doThrow(new DuplicateAccountException(5))
                .when(accountService).createNewAccount(eq(5), any(BigDecimal.class));
        accountHandler.createNewAccount(request);
    }

    @Test
    public void getAccountSuccess() {
        NewAccountRequest request = new NewAccountRequest();
        request.setAccountId(5);
        request.setAmount(new BigDecimal("500"));
        doReturn(new Account(5, new BigDecimal("500")))
                .when(accountService).createNewAccount(eq(5), any(BigDecimal.class));
        accountHandler.createNewAccount(request);
        doReturn(new Account(5, new BigDecimal("500")))
                .when(accountService).getAccount(eq(5));
        Account account = accountHandler.getAccount("5");
        assertEquals(Integer.valueOf(5), account.getAccountId());
        assertEquals(new BigDecimal("500"), account.getAmount());
    }

    @Test(expected = BadRequestException.class)
    public void getAccountNullKey() {
        accountHandler.getAccount(null);
        verifyZeroInteractions(accountService);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getAccountNegativeKey() {
        doThrow(new IllegalArgumentException())
                .when(accountService).getAccount(eq(-1));
        accountHandler.getAccount("-1");
    }

    @Test(expected = BadRequestException.class)
    public void getAccountInvalidKey() {
        accountHandler.getAccount("abacaba");
        verifyZeroInteractions(accountService);
    }

    @Test(expected = AccountNotFoundException.class)
    public void getMissingAccount() {
        doThrow(new AccountNotFoundException(5))
                .when(accountService).getAccount(eq(5));
        accountHandler.getAccount("5");
    }
}
