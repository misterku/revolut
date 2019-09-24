package misterku.revolut.model.exception;


public class DuplicateAccountException extends BadRequestException {
    public DuplicateAccountException(Integer id) {
        super("Account " + id + " is already exist");
    }
}
