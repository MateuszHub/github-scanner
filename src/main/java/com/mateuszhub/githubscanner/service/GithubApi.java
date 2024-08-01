package com.mateuszhub.githubscanner.service;

import com.mateuszhub.githubscanner.exception.GithubApiException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@Service
public class GithubApi {
    private final RestClient restClient;
    private final NextPageUrlExtractor nextPageUrlExtractor;

    @Value("${GH_TOKEN:}")
    private String ghToken;

    public GithubApi(RestClient restClient, NextPageUrlExtractor nextPageUrlExtractor) {
        this.restClient = restClient;
        this.nextPageUrlExtractor = nextPageUrlExtractor;
    }

    public <T> List<T> fetchApiAllPages(String pageUrl, ParameterizedTypeReference<List<T>> responseType) throws GithubApiException {

        List<T> result = new ArrayList<>();
        do {
            try {
                ResponseEntity<List<T>> responseEntity = restClient.get()
                        .uri(pageUrl)
                        .accept(MediaType.APPLICATION_JSON)
                        .headers(setAccessTokenIfPresent())
                        .retrieve()
                        .toEntity(responseType);

                if (responseEntity.getBody() == null) break;
                result.addAll(responseEntity.getBody());
                pageUrl = nextPageUrlExtractor.getNextPageUrl(responseEntity.getHeaders().getFirst("link"));
            } catch (RestClientResponseException e) {
                throwExceptionOnErrorCode(e.getStatusCode());
            }
        } while (pageUrl != null);
        return result;
    }

    private void throwExceptionOnErrorCode(HttpStatusCode statusCode) {
        if(statusCode.isSameCodeAs(HttpStatus.NOT_FOUND))
            throw new GithubApiException(GithubApiException.NOT_FOUND_MESSAGE, HttpStatus.NOT_FOUND);
        if(statusCode.isSameCodeAs(HttpStatus.UNAUTHORIZED) || statusCode.isSameCodeAs(HttpStatus.FORBIDDEN))
            throw new GithubApiException(GithubApiException.API_ACCESS_KEY_EXCEPTION, HttpStatus.INTERNAL_SERVER_ERROR);
        if (statusCode.is4xxClientError() || statusCode.is5xxServerError())
            throw new GithubApiException(GithubApiException.OTHER_API_EXCEPTION, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private Consumer<HttpHeaders> setAccessTokenIfPresent() {
        return headers -> {
            if (StringUtils.hasText(ghToken)) {
                headers.set("Authorization", "Bearer " + ghToken);
            }
        };
    }
}
