package io.github.magicdgs.gaming.ygoprodeck.client.exception;

/**
 * Exceptions thrown by the API client (or downstream consumers if they require it).
 */
public class YgoprodeckException extends RuntimeException {

    public YgoprodeckException(final String msg, final Throwable throwable) {
        super(msg, throwable);
    }

    public YgoprodeckException(final String msg) {
        super(msg);
    }
}
