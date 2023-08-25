package com.tomdud.githubservice.handler;

import com.tomdud.githubservice.dto.ErrorDTO;
import com.tomdud.githubservice.exception.GithubBadRequestException;
import com.tomdud.githubservice.exception.GithubResourceNotFoundException;
import com.tomdud.githubservice.exception.GithubUserNotFoundException;
import com.tomdud.githubservice.exception.UnknownGithubApiException;
import io.netty.handler.codec.http.HttpResponseStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class GithubExceptionHandler {

    private final Logger log = LoggerFactory.getLogger(GithubExceptionHandler.class);

    @ResponseBody
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(GithubUserNotFoundException.class)
    public ResponseEntity<ErrorDTO> handleGithubUserNotFoundException(GithubUserNotFoundException ex) {
        log.error("ExceptionHandler::handleGithubUserNotFoundException caught: {}", ex.getMessage());

        ErrorDTO errorDTO = new ErrorDTO(HttpResponseStatus.NOT_FOUND.code(), ex.getMessage());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorDTO);
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(GithubResourceNotFoundException.class)
    public ResponseEntity<ErrorDTO> handleGithubResourceNotFoundException(GithubResourceNotFoundException ex) {
        log.error("ExceptionHandler::handleGithubResourceNotFoundException caught: {}", ex.getMessage());

        ErrorDTO errorDTO = new ErrorDTO(HttpResponseStatus.NOT_FOUND.code(), ex.getMessage());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorDTO);
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(UnknownGithubApiException.class)
    public ResponseEntity<ErrorDTO> handleUnknownGithubApiException(UnknownGithubApiException ex) {
        log.error("ExceptionHandler::handleUnknownGithubApiException caught: {}", ex.getMessage());

        ErrorDTO errorDTO = new ErrorDTO(HttpResponseStatus.INTERNAL_SERVER_ERROR.code(), ex.getMessage());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorDTO);
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(GithubBadRequestException.class)
    public ResponseEntity<ErrorDTO> handleGithubBadRequestException(GithubBadRequestException ex) {
        log.error("ExceptionHandler::handleGithubBadRequestException caught: {}" ,ex.getMessage());

        ErrorDTO errorDTO = new ErrorDTO(HttpResponseStatus.BAD_REQUEST.code(), ex.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorDTO);
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorDTO> handleRuntimeException(RuntimeException ex) {
        log.error("ExceptionHandler::handleRuntimeException caught: {}" ,ex.getMessage());

        ErrorDTO errorDTO = new ErrorDTO(HttpResponseStatus.INTERNAL_SERVER_ERROR.code(), ex.getMessage());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorDTO);
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    @ExceptionHandler(HttpMediaTypeNotAcceptableException.class)
    public ResponseEntity<ErrorDTO> handleHttpMediaTypeNotAcceptableException() {
        log.error("ExceptionHandler::handleHttpMediaTypeNotAcceptableException caught");

        ErrorDTO errorDTO = new ErrorDTO(
                HttpResponseStatus.NOT_ACCEPTABLE.code(),
                "You provided wrong Accept header, there is a acceptable MIME type:" + MediaType.APPLICATION_JSON_VALUE
        );

        //Create header to prevent error from spring, which check the consistency between returned type and user Accept header
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).headers(headers).body(errorDTO);
    }

}
