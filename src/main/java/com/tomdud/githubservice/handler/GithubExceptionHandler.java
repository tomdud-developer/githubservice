package com.tomdud.githubservice.handler;

import com.tomdud.githubservice.dto.ApiResponse;
import com.tomdud.githubservice.dto.ErrorDTO;
import com.tomdud.githubservice.exception.GithubUserNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class GithubExceptionHandler {

    private final Logger log = LoggerFactory.getLogger(GithubExceptionHandler.class);

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(GithubUserNotFoundException.class)
    public Mono<ResponseEntity<ApiResponse<String>>> handleGithubUserNotFoundException(GithubUserNotFoundException ex) {
        log.error("ExceptionHandler::handleGithubUserNotFoundException caught: {}", ex.getMessage());

        var apiResponse = ApiResponse.<String>builder()
                .results("error")
                .errors(
                        List.of(new ErrorDTO("username", ex.getMessage()))
                ).build();

        return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiResponse));
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(RuntimeException.class)
    public Flux<ApiResponse<String>> handleRuntimeException(RuntimeException ex) {
        log.error("ExceptionHandler::handleRuntimeException caught: {}" ,ex.getMessage());

        var apiResponse = ApiResponse.<String>builder()
                .results("error")
                .errors(
                        List.of(new ErrorDTO("unknown", ex.getMessage()))
                ).build();

        return Flux.just(apiResponse);
    }

}
