package br.com.on.app.exception;

public class MyPersonalException extends RuntimeException {

    public MyPersonalException(String message) {
        super(message);
    }

    public MyPersonalException(String message, Throwable cause) {
        super(message, cause);
    }

}
