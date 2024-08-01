package com.mateuszhub.githubscanner;

import com.mateuszhub.githubscanner.dto.ReposDTO;
import com.mateuszhub.githubscanner.dto.githubapi.BranchResponseDTO;
import com.mateuszhub.githubscanner.dto.githubapi.CommitResponseDTO;
import com.mateuszhub.githubscanner.dto.githubapi.OwnerResponseDTO;
import com.mateuszhub.githubscanner.dto.githubapi.RepoResponseDTO;
import com.mateuszhub.githubscanner.exception.GithubApiException;
import com.mateuszhub.githubscanner.resource.RepoController;
import com.mateuszhub.githubscanner.service.GithubApi;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class GithubScannerApplicationTests {

    @MockBean
    private GithubApi githubApi;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void fetchEmptyRepoList() throws Exception {
        Mockito.when(githubApi.fetchApiAllPages(Mockito.anyString(), Mockito.any())).thenReturn(new ArrayList<>());


        ResponseEntity<String> response = restTemplate.getForEntity("/users/testuser/repositories", String.class);


        Assertions.assertTrue(response.getStatusCode().isSameCodeAs(HttpStatus.OK), "Empty repo list from github should result in status code 200");
    }

    @Test
    void fetchRepoList() {
        List<RepoResponseDTO> reposDTO = List.of(new RepoResponseDTO("branch1-url", false, "testrepo",
                new OwnerResponseDTO("testuser")),new RepoResponseDTO("branch2-url", true, "testrepo-forked",
                new OwnerResponseDTO("testuser")));
        Mockito.when(githubApi.fetchApiAllPages(Mockito.anyString(), Mockito.eq(new ParameterizedTypeReference<List<RepoResponseDTO>>() {})))
                .thenReturn(reposDTO);
        Mockito.when(githubApi.fetchApiAllPages(Mockito.contains("branch1-url"), Mockito.any()))
                .thenReturn(List.of(new BranchResponseDTO("branch1", new CommitResponseDTO("commit1-sha"))));
        Mockito.when(githubApi.fetchApiAllPages(Mockito.contains("branch2-url"), Mockito.any()))
                .thenReturn(List.of(new BranchResponseDTO("branch2", new CommitResponseDTO("commit2-sha"))));


        ResponseEntity<String> response = restTemplate.getForEntity("/users/testuser/repositories", String.class);


        Assertions.assertTrue(response.getStatusCode().isSameCodeAs(HttpStatus.OK), "Repo list from github should result in status code 200");
        Assertions.assertTrue(response.getBody().contains("commit1-sha"), "Body should have commit sha");
        Assertions.assertFalse(response.getBody().contains("commit2-sha"), "Body should not have commit sha from forked repo");
        Assertions.assertTrue(response.getBody().contains("testuser"), "Body should have owner login");
        Assertions.assertTrue(response.getBody().contains("testrepo"), "Body should have repo name");
        Assertions.assertFalse(response.getBody().contains("testrepo-forked"), "Body should not have forked repo name");
    }

    @Test
    void fetchNotExisitingUser() {
        Mockito.when(githubApi.fetchApiAllPages(Mockito.anyString(), Mockito.eq(new ParameterizedTypeReference<List<RepoResponseDTO>>() {})))
                .thenThrow(new GithubApiException(GithubApiException.NOT_FOUND_MESSAGE, HttpStatus.NOT_FOUND));

        ResponseEntity<String> response = restTemplate.getForEntity("/users/testuser/repositories", String.class);

        Assertions.assertTrue(response.getStatusCode().isSameCodeAs(HttpStatus.NOT_FOUND), "Repo should return 404 if user not found");
    }

}
