package com.tomdud.githubservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tomdud.githubservice.dto.githubapi.GithubApiBranchResponseDTO;
import com.tomdud.githubservice.dto.githubapi.GithubApiRepositoriesResponseDTO;
import com.tomdud.githubservice.exception.GithubUserNotFoundException;
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
        List<GithubApiRepositoriesResponseDTO> mockRepositoriesDTOList = new ArrayList<>();
        mockRepositoriesDTOList.add(new GithubApiRepositoriesResponseDTO("test-RepoName1", false));
        mockRepositoriesDTOList.add(new GithubApiRepositoriesResponseDTO("test-RepoName2", true));
        mockRepositoriesDTOList.add(new GithubApiRepositoriesResponseDTO("test-RepoName3", false));

        //when
        mockBackEnd.enqueue(new MockResponse()
                .setBody(objectMapper.writeValueAsString(mockRepositoriesDTOList))
                .addHeader("Content-Type", "application/json")
                .setResponseCode(200)
        );
        Flux<GithubApiRepositoriesResponseDTO> githubApiRepositoriesResponseDTOFlux =
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
        Flux<GithubApiRepositoriesResponseDTO> githubApiRepositoriesResponseDTOFlux =
                githubWebClient.getUserRepositories("test-username");

        //then
        StepVerifier.create(githubApiRepositoriesResponseDTOFlux)
                .expectError(GithubUserNotFoundException.class)
                .verify();
    }

    @Test
    void testGetInformationAboutBranchesInRepositorySuccess() throws JsonProcessingException {
        //given
        List<GithubApiBranchResponseDTO> mockBranchesDTOList = new ArrayList<>();
        mockBranchesDTOList.add(new GithubApiBranchResponseDTO("test-BranchName1", "313aeac31f14bb4542c035438fcc1f9753bb7e08"));
        mockBranchesDTOList.add(new GithubApiBranchResponseDTO("test-BranchName2", "04b24aaf72602f7cc978de48c797967501c8444f"));
        mockBranchesDTOList.add(new GithubApiBranchResponseDTO("test-BranchName3", "30e707192cb80485853f7024756d6b7e4eb02069"));

        //when
        mockBackEnd.enqueue(new MockResponse()
                .setBody(objectMapper.writeValueAsString(mockBranchesDTOList))
                .addHeader("Content-Type", "application/json")
                .setResponseCode(200)
        );
        Flux<GithubApiBranchResponseDTO> githubApiRepositoriesResponseDTOFlux =
                githubWebClient.getInformationAboutBranchesInRepository("test-username", "test-repository-name");

        //then
        StepVerifier.create(githubApiRepositoriesResponseDTOFlux)
                .expectNextMatches(repository -> repository.equals(mockBranchesDTOList.get(0)))
                .expectNextMatches(repository -> repository.equals(mockBranchesDTOList.get(1)))
                .expectNextMatches(repository -> repository.equals(mockBranchesDTOList.get(2)))
                .verifyComplete();
    }

    @Test
    void testGetInformationAboutBranchesInRepositorySuccessBecauseOfNotExistingUserOrRepository() throws JsonProcessingException {
        //when
        mockBackEnd.enqueue(new MockResponse()
                .setResponseCode(404)
        );
        Flux<GithubApiBranchResponseDTO> githubApiRepositoriesResponseDTOFlux =
                githubWebClient.getInformationAboutBranchesInRepository("test-username", "test-repository-name");

        //then
        StepVerifier.create(githubApiRepositoriesResponseDTOFlux)
                .expectError(GithubUserNotFoundException.class)
                .verify();
    }

}