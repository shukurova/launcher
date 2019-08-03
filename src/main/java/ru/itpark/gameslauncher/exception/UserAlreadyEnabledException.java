package ru.itpark.gameslauncher.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class UserAlreadyEnabledException extends RuntimeException {
    public UserAlreadyEnabledException() {
    }

    public UserAlreadyEnabledException(String message) {
        super(message);
    }

    public UserAlreadyEnabledException(String message, Throwable cause) {
        super(message, cause);
    }

    public UserAlreadyEnabledException(Throwable cause) {
        super(cause);
    }

    public UserAlreadyEnabledException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
