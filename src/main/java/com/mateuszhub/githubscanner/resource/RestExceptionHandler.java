package com.mateuszhub.githubscanner.resource;

import com.mateuszhub.githubscanner.exception.GithubApiException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class RestExceptionHandler {
    public record RestException(Integer status, String message) {
    }

    @ExceptionHandler(GithubApiException.class)
    public ResponseEntity<RestException> handleGithubApiException(GithubApiException e) {
        return new ResponseEntity<>(new RestException(e.getStatus().value(), e.getMessage()), HttpStatus.NOT_FOUND);
    }
}
