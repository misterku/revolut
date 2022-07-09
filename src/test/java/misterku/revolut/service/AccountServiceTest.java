package misterku.revolut.service;

import misterku.revolut.model.Account;
import misterku.revolut.model.exception.AccountNotFoundException;
import misterku.revolut.model.exception.DuplicateAccountException;
import misterku.revolut.model.service.TransferResult;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.*;

public class AccountServiceTest {
    private AccountService accountService;

    @Before
    public void setUp() {
        accountService = new AccountService();
    }

    @Test
    public void testCreateNormalAccount() {
        final var account = accountService.createNewAccount(1, new BigDecimal("100"));
        assertNotNull(account);
        assertEquals(Integer.valueOf(1), account.getAccountId());
        assertEquals(new BigDecimal("100"), account.getAmount());
    }

    @Test(expected = DuplicateAccountException.class)
    public void testCreateAccountWithExistingAccountId() {
        accountService.createNewAccount(1, new BigDecimal("100"));
        accountService.createNewAccount(1, new BigDecimal("200"));
    }

    @Test
    public void testGetAccountSuccess() {
        accountService.createNewAccount(100, new BigDecimal("100"));
        final var account = accountService.getAccount(100);
        assertNotNull(account);
        assertEquals(Integer.valueOf(100), account.getAccountId());
        assertEquals(new BigDecimal("100"), account.getAmount());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetAccountIdIsNull() {
        accountService.createNewAccount(100, new BigDecimal("100"));
        accountService.getAccount(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetAccountIdIsNegative() {
        accountService.createNewAccount(100, new BigDecimal("100"));
        accountService.getAccount(-10);
    }

    @Test(expected = AccountNotFoundException.class)
    public void testGetMissingAccount() {
        accountService.getAccount(10);
    }

    @Test(expected = AccountNotFoundException.class)
    public void testTransferInvalidSrcId() throws Exception {
        accountService.createNewAccount(1, new BigDecimal("100"));
        accountService.createNewAccount(2, new BigDecimal("100"));
        accountService.transfer(5, 2, new BigDecimal("10"));
    }

    @Test(expected = AccountNotFoundException.class)
    public void testTransferInvalidDstId() throws Exception {
        accountService.createNewAccount(1, new BigDecimal("100"));
        accountService.createNewAccount(2, new BigDecimal("100"));
        accountService.transfer(1, 5, new BigDecimal("10"));
    }

    @Test
    public void testTransferSuccess() throws Exception {
        accountService.createNewAccount(1, new BigDecimal("100"));
        accountService.createNewAccount(2, new BigDecimal("100"));
        final var transferResult = accountService.transfer(1, 2, new BigDecimal("50"));
        assertTrue(transferResult.success());
        final var account1 = accountService.getAccount(1);
        final var account2 = accountService.getAccount(2);
        assertEquals(new BigDecimal("50"), account1.getAmount());
        assertEquals(new BigDecimal("150"), account2.getAmount());
    }

    @Test
    public void testTransferNotEnoughMoney() throws Exception {
        accountService.createNewAccount(1, new BigDecimal("20"));
        accountService.createNewAccount(2, new BigDecimal("20"));
        final var transferResult = accountService.transfer(1, 2, new BigDecimal("50"));
        assertFalse(transferResult.success());
        final var account1 = accountService.getAccount(1);
        final var account2 = accountService.getAccount(2);
        assertEquals(new BigDecimal("20"), account1.getAmount());
        assertEquals(new BigDecimal("20"), account2.getAmount());
    }

    @Test
    public void testTransferSameSrcAndDst() throws Exception {
        accountService.createNewAccount(1, new BigDecimal("20"));
        final var transferResult = accountService.transfer(1, 1, new BigDecimal("10"));
        assertFalse(transferResult.success());
    }




}
