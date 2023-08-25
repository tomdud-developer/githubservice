package com.tomdud.githubservice.exception;

public class GithubBadRequestException extends RuntimeException {
    public GithubBadRequestException(String message) {
        super(message);
    }
}
