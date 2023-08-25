package com.tomdud.githubservice.service;

import com.tomdud.githubservice.dto.GithubRepositoryResponseDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class GithubServiceTest {

    @Autowired
    GithubService githubService;

    @Test
    void getUserNotForkedRepositoriesInformation() {
        Flux<GithubRepositoryResponseDTO> repositoryFlux = githubService.getUserNotForkedRepositoriesInformation("tomdud-developer");

        Mono<List<GithubRepositoryResponseDTO>> repositoryListMono = repositoryFlux.collectList();

        List<GithubRepositoryResponseDTO> list = repositoryListMono.block();


        System.out.println("End");

    }
}