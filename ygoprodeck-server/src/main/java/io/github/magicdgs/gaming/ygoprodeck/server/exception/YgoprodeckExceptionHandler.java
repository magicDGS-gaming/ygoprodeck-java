package io.github.magicdgs.gaming.ygoprodeck.server.exception;

import io.github.magicdgs.gaming.ygoprodeck.model.exception.YgoprodeckResponseErrorException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class YgoprodeckExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(YgoprodeckResponseErrorException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Object> handleYgoprodeckResponseErrorException(YgoprodeckResponseErrorException ex) {
        return ResponseEntity.badRequest().body(ex.getError());
    }

}
