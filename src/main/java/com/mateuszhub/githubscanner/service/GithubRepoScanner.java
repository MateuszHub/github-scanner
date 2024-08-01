package com.mateuszhub.githubscanner.service;

import com.mateuszhub.githubscanner.dto.BranchDTO;
import com.mateuszhub.githubscanner.dto.RepoDTO;
import com.mateuszhub.githubscanner.dto.ReposDTO;
import com.mateuszhub.githubscanner.dto.githubapi.BranchResponseDTO;
import com.mateuszhub.githubscanner.dto.githubapi.RepoResponseDTO;
import com.mateuszhub.githubscanner.exception.GithubApiException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;


@Service
public class GithubRepoScanner {
    private final static String BASE_REPOS_URL = "https://api.github.com/users/%s/repos";
    private final GithubApi githubApi;

    public GithubRepoScanner(GithubApi githubApi) {
        this.githubApi = githubApi;
    }

    public ReposDTO getAllOriginalRepositories(String username) {
        String pageUrl = BASE_REPOS_URL.formatted(username);
        List<RepoResponseDTO> repos = new ArrayList<>();

        repos = githubApi.fetchApiAllPages(pageUrl, new ParameterizedTypeReference<List<RepoResponseDTO>>() {});

        List<RepoDTO> repoitoriesDTO = repos.stream()
                .filter(repo -> !repo.fork())
                .map(repo -> new RepoDTO(repo.name(), repo.owner().login(), getAllBranches(repo.url())))
                .toList();

        return new ReposDTO(repoitoriesDTO);
    }

    private List<BranchDTO> getAllBranches(String repoUrl) {
        String pageUrl = repoUrl + "/branches";
        List<BranchResponseDTO> branches = new ArrayList<>();

        try {
            branches = githubApi.fetchApiAllPages(pageUrl,new ParameterizedTypeReference<List<BranchResponseDTO>>() {});
        } catch (RestClientResponseException|GithubApiException e) {
            return Collections.emptyList();
        }

        return branches.stream()
                .map(branch -> new BranchDTO(branch.name(), branch.commit().sha()))
                .toList();
    }






}
