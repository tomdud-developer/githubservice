package com.tomdud.githubservice.exception;

public class UnknownGithubApiException extends RuntimeException {
    public UnknownGithubApiException(String message) {
        super(message);
    }
}
