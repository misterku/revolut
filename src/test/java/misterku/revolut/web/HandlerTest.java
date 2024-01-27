package misterku.revolut.web;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import misterku.revolut.model.Account;
import misterku.revolut.model.http.NewAccountRequest;
import misterku.revolut.model.http.TransferRequest;
import misterku.revolut.model.service.TransferResult;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.*;

public class HandlerTest {

    public static final int amountPerAccount = 1000;
    public static final int numberOfJobs = 100000;
    private OkHttpClient httpClient;
    private Gson gson;
    private Handlers handlers;

    private static final int portNumber = 8080;

    private static final int numberOfAccounts = 100;

    @Before
    public void setUp() {
        httpClient = new OkHttpClient.Builder()
                .followRedirects(false)
                .build();

        gson = new Gson();
        handlers = new Handlers();
    }

    public Response sendPost(String body, String path) throws IOException {
        Request request = new Request.Builder()
                .url("http://localhost:" + portNumber + path)
                .post(RequestBody.create(body, MediaType.get("application/json")))
                .build();
        return httpClient.newCall(request).execute();
    }

    public Response sendGet(String path) throws IOException {
        Request request = new Request.Builder()
                .url("http://localhost:" + portNumber + path)
                .get()
                .build();
        return httpClient.newCall(request).execute();
    }

    public Account createNewAccount(Integer accountId, BigDecimal amount) throws IOException {
        NewAccountRequest request = new NewAccountRequest();
        request.setAmount(amount);
        request.setAccountId(accountId);
        Response response = sendPost(gson.toJson(request), "/accounts");
        if (response.isSuccessful()) {
            return gson.fromJson(response.body().string(), Account.class);
        } else {
            throw new RuntimeException("Failed new account request");
        }
    }

    public Account getAccountData(Integer accountId) throws IOException {
        String path = "/accounts/" + accountId;
        Response response = sendGet(path);
        if (response.isSuccessful()) {
            Account account = gson.fromJson(response.body().string(), Account.class);
            response.close();
            return account;
        } else {
            throw new RuntimeException("Failed request");
        }
    }

    public TransferResult transfer(Integer sourceId, Integer destinationId, BigDecimal amount) throws IOException {
        String path = "/transfer";
        TransferRequest request = new TransferRequest();
        request.setAmount(amount);
        request.setSourceId(sourceId);
        request.setDestinationId(destinationId);
        Response response = sendPost(gson.toJson(request), path);
        if (response.isSuccessful()) {
            JsonObject object = gson.fromJson(response.body().string(), JsonObject.class);
            response.close();
            return new TransferResult(object.get("success").getAsBoolean(), object.get("message").getAsString());
        } else {
            throw new RuntimeException("Failed request");
        }
    }

    @Test
    public void testCreateNewAccount() throws IOException {
        Account account = createNewAccount(1, new BigDecimal("100"));
        assertNotNull(account);
        assertEquals(Integer.valueOf(1), account.getAccountId());
        assertEquals(new BigDecimal("100"), account.getAmount());
    }

    @Test(expected = RuntimeException.class)
    public void testFailure() throws IOException {
        createNewAccount(2, new BigDecimal("100"));
        createNewAccount(2, new BigDecimal("100"));
    }

    @Test
    public void testCreateNewAccount2() throws IOException {
        createNewAccount(1, new BigDecimal("100"));
        Account account = getAccountData(1);
        assertNotNull(account);
        assertEquals(Integer.valueOf(1), account.getAccountId());
        assertEquals(new BigDecimal("100"), account.getAmount());
    }

    @Test
    public void loadTest() throws IOException, ExecutionException, InterruptedException {
        createAccounts();
        AtomicInteger failures = new AtomicInteger();
        List<Future<TransferResult>> jobs = runJobs(failures);
        waitForResults(jobs);
        BigDecimal result = overallAmount();

        assertEquals(BigDecimal.valueOf(amountPerAccount * numberOfAccounts), result);
        assertTrue(failures.get() < numberOfJobs / 4);
    }

    @NotNull
    private List<Future<TransferResult>> runJobs(AtomicInteger failures) {
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        Random random = ThreadLocalRandom.current();
        List<Future<TransferResult>> results = new ArrayList<>();
        for (int i = 0; i < numberOfJobs; i++) {
            results.add(executorService.submit(() -> {
                try {
                    return transfer(1 + random.nextInt(numberOfAccounts), 1 + random.nextInt(numberOfAccounts), BigDecimal.valueOf(1 + random.nextInt(100)));
                } catch (IOException | RuntimeException e) {
                    failures.incrementAndGet();
                    return null;
                }
            }));
        }
        return results;
    }

    private BigDecimal overallAmount() throws IOException {
        BigDecimal result = BigDecimal.ZERO;
        for (int i = 1; i <= numberOfAccounts; i++) {
            Account account = getAccountData(i);
            result = result.add(account.getAmount());
        }
        return result;
    }

    private void waitForResults(List<Future<TransferResult>> results) throws InterruptedException, ExecutionException {
        for (Future<TransferResult> result : results) {
            result.get();
        }
    }

    private void createAccounts() throws IOException {
        for (int i = 1; i <= numberOfAccounts; i++) {
            createNewAccount(i, BigDecimal.valueOf(amountPerAccount));
        }
    }

    @After
    public void tearDown() {
        handlers.stop();
    }

}
