package com.github.mehrdadfalahati.exception;

public class OrmServiceException extends RuntimeException {
    public OrmServiceException() {
    }

    public OrmServiceException(String message) {
        super(message);
    }

    public OrmServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public OrmServiceException(Throwable cause) {
        super(cause);
    }
}
