package com.breedish.bet;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.io.FileNotFoundException;
import java.io.IOException;

import static org.springframework.http.ResponseEntity.*;

/**
 * @author zenind
 */
@ControllerAdvice
public class BetExceptionHandler extends ResponseEntityExceptionHandler {

    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    @ExceptionHandler({FileNotFoundException.class, IOException.class})
    public ResponseEntity<Void> handleNotFound() {
        return notFound().build();
    }

    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ExceptionHandler({IllegalArgumentException.class})
    public ResponseEntity<Void> handleBadRequest(Exception exception) {
        return badRequest().build();
    }

}
