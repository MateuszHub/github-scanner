package com.mateuszhub.githubscanner.resource;

import com.mateuszhub.githubscanner.dto.ReposDTO;
import com.mateuszhub.githubscanner.service.GithubRepoScanner;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RepoController {

    private final GithubRepoScanner repoScanner;

    public RepoController(GithubRepoScanner repoScanner) {
        this.repoScanner = repoScanner;
    }

    @GetMapping(value = "/users/{username}/repositories", produces = "application/json")
    ResponseEntity<ReposDTO> getRepos(@PathVariable String username) {
        ReposDTO allOriginalRepositories = repoScanner.getAllOriginalRepositories(username);
        return ResponseEntity.ok(allOriginalRepositories);
    }


}
