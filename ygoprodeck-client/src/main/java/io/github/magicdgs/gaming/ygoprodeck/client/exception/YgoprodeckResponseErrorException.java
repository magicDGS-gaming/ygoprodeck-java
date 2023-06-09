package io.github.magicdgs.gaming.ygoprodeck.client.exception;

import io.github.magicdgs.gaming.ygoprodeck.model.ErrorDTO;

public class YgoprodeckResponseErrorException extends YgoprodeckException {

    private final ErrorDTO errorDto;

    public YgoprodeckResponseErrorException(ErrorDTO errorDto) {
        super(errorDto.getError());
        this.errorDto = errorDto;
    }

    public ErrorDTO getError() {
        return errorDto;
    }
}
