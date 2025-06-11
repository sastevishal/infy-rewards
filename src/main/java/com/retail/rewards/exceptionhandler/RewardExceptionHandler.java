package com.retail.rewards.exceptionhandler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.time.format.DateTimeParseException;

/**
 * Global exception handler for the Rewards API.
 *
 * This class uses @RestControllerAdvice to intercept exceptions thrown from any controller
 * and returns a structured and meaningful error response to the client.
 *
 * Handled Exceptions:
 * - DateTimeParseException: Triggered when the input date format is invalid.
 * - IllegalArgumentException: Triggered for invalid arguments like start date after end date.
 * - NoTransactionFoundException: Custom exception thrown when no transactions are found.
 * - Exception: Catches all other unhandled exceptions as a fallback.
 *
 * Each handler returns an appropriate HTTP status code and a user-friendly error message.
 */

@RestControllerAdvice
public class RewardExceptionHandler {

    @ExceptionHandler(DateTimeParseException.class)
    public ResponseEntity<ErrorResponse> handleInvalidDate(DateTimeParseException ex) {
        ErrorResponse error = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Invalid date format. Expected format: yyyy-MM-dd"
        );
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException ex) {
        ErrorResponse error = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                ex.getMessage()
        );
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        ErrorResponse error = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Something went wrong: " + ex.getMessage()
        );
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(NoTransactionFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoTransaction(NoTransactionFoundException ex) {
        return buildResponse(HttpStatus.NO_CONTENT, ex.getMessage());
    }

    private ResponseEntity<ErrorResponse> buildResponse(HttpStatus status, String message) {
        return new ResponseEntity<>(new ErrorResponse(status.value(), message), status);
    }
}
