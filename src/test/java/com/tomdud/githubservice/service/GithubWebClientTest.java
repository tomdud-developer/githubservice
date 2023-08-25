package com.tomdud.githubservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tomdud.githubservice.dto.githubapi.GithubApiBranchResponseRecord;
import com.tomdud.githubservice.dto.githubapi.GithubApiRepositoriesResponseRecord;
import com.tomdud.githubservice.exception.GithubResourceNotFoundException;
import com.tomdud.githubservice.exception.GithubUserNotFoundException;
import com.tomdud.githubservice.exception.UnknownGithubApiException;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
class GithubWebClientTest {

    public static MockWebServer mockBackEnd;

    @Autowired
    GithubWebClient githubWebClient;

    ObjectMapper objectMapper = new ObjectMapper();

    @BeforeAll
    static void setUp() throws IOException {
        mockBackEnd = new MockWebServer();
        mockBackEnd.start();
    }

    @AfterAll
    static void tearDown() throws IOException {
        mockBackEnd.shutdown();
    }

    @BeforeEach
    void initialize() {
        githubWebClient = new GithubWebClient(
                WebClient.builder(),
                mockBackEnd.url("/").toString(),
                "test-version");
    }

    @Test
    void testGetUserRepositoriesSuccess() throws JsonProcessingException {
        //given
        List<GithubApiRepositoriesResponseRecord> mockRepositoriesDTOList = new ArrayList<>();
        mockRepositoriesDTOList.add(new GithubApiRepositoriesResponseRecord("test-RepoName1", false));
        mockRepositoriesDTOList.add(new GithubApiRepositoriesResponseRecord("test-RepoName2", true));
        mockRepositoriesDTOList.add(new GithubApiRepositoriesResponseRecord("test-RepoName3", false));

        //when
        mockBackEnd.enqueue(new MockResponse()
                .setBody(objectMapper.writeValueAsString(mockRepositoriesDTOList))
                .addHeader("Content-Type", "application/json")
                .setResponseCode(200)
        );
        Flux<GithubApiRepositoriesResponseRecord> githubApiRepositoriesResponseDTOFlux =
                githubWebClient.getUserRepositories("test-username");

        //then
        StepVerifier.create(githubApiRepositoriesResponseDTOFlux)
                    .expectNextMatches(repository -> repository.equals(mockRepositoriesDTOList.get(0)))
                    .expectNextMatches(repository -> repository.equals(mockRepositoriesDTOList.get(1)))
                    .expectNextMatches(repository -> repository.equals(mockRepositoriesDTOList.get(2)))
                    .verifyComplete();
    }

    @Test
    void testGetUserRepositoriesFailedBecauseOfNotExistingUser() {
        //when
        mockBackEnd.enqueue(new MockResponse()
                .setResponseCode(404)
        );
        Flux<GithubApiRepositoriesResponseRecord> githubApiRepositoriesResponseDTOFlux =
                githubWebClient.getUserRepositories("test-username");

        //then
        StepVerifier.create(githubApiRepositoriesResponseDTOFlux)
                .expectError(GithubUserNotFoundException.class)
                .verify();
    }

    @Test
    void testGetUserRepositoriesFailedBecauseOfGithubExternalError() {
        //when
        mockBackEnd.enqueue(new MockResponse()
                .setResponseCode(500)
        );
        Flux<GithubApiRepositoriesResponseRecord> githubApiRepositoriesResponseDTOFlux =
                githubWebClient.getUserRepositories("test-username");

        //then
        StepVerifier.create(githubApiRepositoriesResponseDTOFlux)
                .expectError(UnknownGithubApiException.class)
                .verify();
    }

    @Test
    void testGetInformationAboutBranchesInRepositorySuccess() throws JsonProcessingException {
        //given
        List<GithubApiBranchResponseRecord> mockBranchesDTOList = new ArrayList<>();
        mockBranchesDTOList.add(new GithubApiBranchResponseRecord("test-BranchName1",
                new GithubApiBranchResponseRecord.Commit("313aeac31f14bb4542c035438fcc1f9753bb7e08")));
        mockBranchesDTOList.add(new GithubApiBranchResponseRecord("test-BranchName2",
                new GithubApiBranchResponseRecord.Commit("04b24aaf72602f7cc978de48c797967501c8444f")));
        mockBranchesDTOList.add(new GithubApiBranchResponseRecord("test-BranchName3",
                new GithubApiBranchResponseRecord.Commit("30e707192cb80485853f7024756d6b7e4eb02069")));

        //when
        mockBackEnd.enqueue(new MockResponse()
                .setBody(objectMapper.writeValueAsString(mockBranchesDTOList))
                .addHeader("Content-Type", "application/json")
                .setResponseCode(200)
        );
        Flux<GithubApiBranchResponseRecord> githubApiRepositoriesResponseDTOFlux =
                githubWebClient.getInformationAboutBranchesInRepository("test-username", "test-repository-name");

        //then
        StepVerifier.create(githubApiRepositoriesResponseDTOFlux)
                .expectNextMatches(repository -> repository.equals(mockBranchesDTOList.get(0)))
                .expectNextMatches(repository -> repository.equals(mockBranchesDTOList.get(1)))
                .expectNextMatches(repository -> repository.equals(mockBranchesDTOList.get(2)))
                .verifyComplete();
    }

    @Test
    void testGetInformationAboutBranchesInRepositoryFailedBecauseOfNotExistingUserOrRepository() throws JsonProcessingException {
        //when
        mockBackEnd.enqueue(new MockResponse()
                .setResponseCode(404)
        );
        Flux<GithubApiBranchResponseRecord> githubApiRepositoriesResponseDTOFlux =
                githubWebClient.getInformationAboutBranchesInRepository("test-username", "test-repository-name");

        //then
        StepVerifier.create(githubApiRepositoriesResponseDTOFlux)
                .expectError(GithubResourceNotFoundException.class)
                .verify();
    }

    @Test
    void testGetInformationAboutBranchesInRepositoryFailedBecauseOfGithubServerError() throws JsonProcessingException {
        //when
        mockBackEnd.enqueue(new MockResponse()
                .setResponseCode(500)
        );
        Flux<GithubApiBranchResponseRecord> githubApiRepositoriesResponseDTOFlux =
                githubWebClient.getInformationAboutBranchesInRepository("test-username", "test-repository-name");

        //then
        StepVerifier.create(githubApiRepositoriesResponseDTOFlux)
                .expectError(UnknownGithubApiException.class)
                .verify();
    }

}