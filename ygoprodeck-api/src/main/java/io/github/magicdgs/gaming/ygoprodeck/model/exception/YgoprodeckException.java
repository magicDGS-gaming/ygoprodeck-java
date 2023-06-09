package io.github.magicdgs.gaming.ygoprodeck.model.exception;

public class YgoprodeckException extends RuntimeException {

    public YgoprodeckException(final String msg, final Throwable throwable) {
        super(msg, throwable);
    }

    public YgoprodeckException(final String msg) {
        super(msg);
    }
}
