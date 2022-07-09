package misterku.revolut.model.exception;


public class AccountNotFoundException extends NotFoundException {
    public AccountNotFoundException(Integer id) {
        super("Account " + id + " is not exists");
    }
}
