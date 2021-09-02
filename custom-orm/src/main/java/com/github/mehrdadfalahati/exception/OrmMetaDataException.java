package com.github.mehrdadfalahati.exception;

public class OrmMetaDataException extends RuntimeException {
    public OrmMetaDataException() {
    }

    public OrmMetaDataException(String message) {
        super(message);
    }

    public OrmMetaDataException(String message, Throwable cause) {
        super(message, cause);
    }

    public OrmMetaDataException(Throwable cause) {
        super(cause);
    }
}
