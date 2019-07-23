package ru.itpark.gameslauncher.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class AccountNotEnabledException extends RuntimeException {
    public AccountNotEnabledException() {
    }

    public AccountNotEnabledException(String message) {
        super(message);
    }

    public AccountNotEnabledException(String message, Throwable cause) {
        super(message, cause);
    }

    public AccountNotEnabledException(Throwable cause) {
        super(cause);
    }

    public AccountNotEnabledException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
