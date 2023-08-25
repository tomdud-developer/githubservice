package com.tomdud.githubservice.exception;

public class GithubResourceNotFoundException extends RuntimeException {
    public GithubResourceNotFoundException(String message) {
        super(message);
    }
}