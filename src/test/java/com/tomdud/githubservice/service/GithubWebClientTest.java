package com.tomdud.githubservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tomdud.githubservice.dto.githubapi.GithubApiRepositoriesResponseDTO;
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

import static org.junit.jupiter.api.Assertions.*;

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
        String baseUrl = String.format("https://api.github.com");
    }

    @Test
    void getUserRepositories() throws JsonProcessingException {
        //given
        List<GithubApiRepositoriesResponseDTO> mockRepositoriesDTOList = new ArrayList<>();
        mockRepositoriesDTOList.add(new GithubApiRepositoriesResponseDTO("test-RepoName1"));
        mockRepositoriesDTOList.add(new GithubApiRepositoriesResponseDTO("test-RepoName2"));
        mockRepositoriesDTOList.add(new GithubApiRepositoriesResponseDTO("test-RepoName3"));

        //when
        mockBackEnd.enqueue(new MockResponse()
                .setBody(objectMapper.writeValueAsString(mockRepositoriesDTOList))
                .addHeader("Content-Type", "application/json"));
        Flux<GithubApiRepositoriesResponseDTO> githubApiRepositoriesResponseDTOFlux =
                githubWebClient.getUserRepositories("tomdud-developer");

        //then
        StepVerifier.create(githubApiRepositoriesResponseDTOFlux)
                    .expectNextCount(3)
                    .expectNextMatches(repository -> repository.name().equals(mockRepositoriesDTOList.get(0).name()))
                    .expectNextMatches(repository -> repository.name().equals(mockRepositoriesDTOList.get(1).name()))
                    .expectNextMatches(repository -> repository.name().equals(mockRepositoriesDTOList.get(2).name()))
                    .verifyComplete();
    }
}