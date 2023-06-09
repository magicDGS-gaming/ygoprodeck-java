package io.github.magicdgs.gaming.ygoprodeck.client.exception;

import io.github.magicdgs.gaming.ygoprodeck.model.ErrorDTO;

/**
 * Exceptions thrown by the client if the server responded with an error object.
 */
public class YgoprodeckResponseErrorException extends YgoprodeckException {

    private final ErrorDTO errorDto;

    public YgoprodeckResponseErrorException(ErrorDTO errorDto) {
        super(errorDto.getError());
        this.errorDto = errorDto;
    }

    /**
     * Gets the error object from the API call.
     *
     * @return error object.
     */
    public ErrorDTO getError() {
        return errorDto;
    }
}
