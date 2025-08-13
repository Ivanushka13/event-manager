package ru.davydov.eventmanger.web;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<ErrorMessageResponse> handleCommonException(Exception e) {
        log.error("Handle common exception", e);
        ErrorMessageResponse messageResponse = new ErrorMessageResponse(
                "Internal error",
                e.getMessage(),
                LocalDateTime.now()
        );
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(messageResponse);
    }

    @ExceptionHandler(value = EntityNotFoundException.class)
    public ResponseEntity<ErrorMessageResponse> handleEntityNotFound(EntityNotFoundException e) {
        log.error("Handle entity not found exception", e);
        ErrorMessageResponse messageResponse = new ErrorMessageResponse(
                "Entity not found",
                e.getMessage(),
                LocalDateTime.now()
        );
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(messageResponse);
    }

    @ExceptionHandler(value = {
            IllegalArgumentException.class,
            MethodArgumentNotValidException.class
    })
    public ResponseEntity<ErrorMessageResponse> handleIllegalArgument(Exception e) {
        log.error("Handle bad request exception", e);
        ErrorMessageResponse messageResponse = new ErrorMessageResponse(
                "Bad request",
                e.getMessage(),
                LocalDateTime.now()
        );
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(messageResponse);
    }

    @ExceptionHandler(value = BadCredentialsException.class)
    public ResponseEntity<ErrorMessageResponse> handleBadCredentials(BadCredentialsException e) {
        log.error("Handle bad credentials exception", e);
        ErrorMessageResponse messageResponse = new ErrorMessageResponse(
                "Failed to authenticate",
                e.getMessage(),
                LocalDateTime.now()
        );
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(messageResponse);
    }

}
