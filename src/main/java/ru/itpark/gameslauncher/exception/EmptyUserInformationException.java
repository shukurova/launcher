package ru.itpark.gameslauncher.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class EmptyUserInformationException extends RuntimeException {
    public EmptyUserInformationException() {
    }

    public EmptyUserInformationException(String message) {
        super(message);
    }

    public EmptyUserInformationException(String message, Throwable cause) {
        super(message, cause);
    }

    public EmptyUserInformationException(Throwable cause) {
        super(cause);
    }

    public EmptyUserInformationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
