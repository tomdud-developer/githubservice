package com.tomdud.githubservice.handler;

import com.tomdud.githubservice.dto.ErrorDTO;
import com.tomdud.githubservice.exception.GithubUserNotFoundException;
import io.netty.handler.codec.http.HttpResponseStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class GithubExceptionHandler {

    private final Logger log = LoggerFactory.getLogger(GithubExceptionHandler.class);

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(GithubUserNotFoundException.class)
    public ResponseEntity<ErrorDTO> handleGithubUserNotFoundException(GithubUserNotFoundException ex) {
        log.error("ExceptionHandler::handleGithubUserNotFoundException caught: {}", ex.getMessage());

        ErrorDTO errorDTO = new ErrorDTO(HttpResponseStatus.NOT_FOUND.code(), ex.getMessage());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorDTO);
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorDTO> handleRuntimeException(RuntimeException ex) {
        log.error("ExceptionHandler::handleRuntimeException caught: {}" ,ex.getMessage());

        ErrorDTO errorDTO = new ErrorDTO(HttpResponseStatus.INTERNAL_SERVER_ERROR.code(), ex.getMessage());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorDTO);
    }

}
