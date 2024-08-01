package com.mateuszhub.githubscanner.exception;

import org.springframework.http.HttpStatus;

public class GithubApiException extends RuntimeException {
    public static final String NOT_FOUND_MESSAGE = "Github API returned 404. Please check the username and try again.";
    public static final String API_ACCESS_KEY_EXCEPTION = "Github API access key is not valid. Please check and try again.";
    public static final String OTHER_API_EXCEPTION = "Github API Exception";

    private final HttpStatus status;

    public GithubApiException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
