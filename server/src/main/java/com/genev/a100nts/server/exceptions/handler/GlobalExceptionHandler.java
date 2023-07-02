package com.genev.a100nts.server.exceptions.handler;

import com.genev.a100nts.server.exceptions.ClientException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;

import javax.mail.MessagingException;
import javax.validation.ConstraintViolationException;
import java.util.Objects;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    private static final String MESSAGE_TEMPLATE = "(path \"%s\") %s";

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleMethodArgumentNotValidException(MethodArgumentNotValidException e, ServletWebRequest request) {
        String message = String.format(MESSAGE_TEMPLATE, request.getRequest().getRequestURI(),
                Objects.requireNonNull(e.getFieldError()).getDefaultMessage());
        log.error("Request with invalid argument – " + message, e);
        return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<String> handleConstraintViolationException(ConstraintViolationException e, ServletWebRequest request) {
        String message = String.format(MESSAGE_TEMPLATE, request.getRequest().getRequestURI(), e.getMessage());
        log.error("Request with invalid argument – " + message, e);
        return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<String> handleHttpMessageNotReadableException(HttpMessageNotReadableException e, ServletWebRequest request) {
        String message = String.format(MESSAGE_TEMPLATE, request.getRequest().getRequestURI(),
                e.getMostSpecificCause().getMessage());
        log.error("Invalid request – " + message, e);
        return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ClientException.class)
    public ResponseEntity<String> handleClientException(ClientException e, ServletWebRequest request) {
        String message = String.format(MESSAGE_TEMPLATE, request.getRequest().getRequestURI(), e.getMessage());
        log.error("Invalid request – " + message, e);
        return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MessagingException.class)
    public ResponseEntity<String> handleMessagingException(MessagingException e, ServletWebRequest request) {
        String message = String.format(MESSAGE_TEMPLATE, request.getRequest().getRequestURI(), e.getMessage());
        log.error("An unexpected error occurred with the mail service – " + message, e);
        return new ResponseEntity<>(message, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
