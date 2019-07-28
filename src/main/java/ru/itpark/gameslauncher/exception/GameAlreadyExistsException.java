package ru.itpark.gameslauncher.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class GameAlreadyExistsException extends RuntimeException {
    public GameAlreadyExistsException() {
    }

    public GameAlreadyExistsException(String message) {
        super(message);
    }

    public GameAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }

    public GameAlreadyExistsException(Throwable cause) {
        super(cause);
    }

    public GameAlreadyExistsException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
