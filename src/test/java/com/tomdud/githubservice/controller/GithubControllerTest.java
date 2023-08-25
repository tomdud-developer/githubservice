package com.tomdud.githubservice.controller;

import com.tomdud.githubservice.dto.RepositoryResponseDTO;
import com.tomdud.githubservice.dto.githubapi.GithubApiBranchResponseRecord;
import com.tomdud.githubservice.dto.githubapi.GithubApiRepositoriesResponseRecord;
import com.tomdud.githubservice.exception.GithubUserNotFoundException;
import com.tomdud.githubservice.service.GithubService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import java.util.stream.Stream;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class GithubControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    GithubService githubService;

    private final String CONTROLLER_BASE_URL = "/github/repositories";
    private final String TEST_USERNAME = "test-username";

    //Declaring mocked responses
    private RepositoryResponseDTO repositoryResponseDTO1;
    private RepositoryResponseDTO repositoryResponseDTO2;

    @BeforeEach
    void setUp() {
        //Setting up mocked repos
        GithubApiRepositoriesResponseRecord githubApiRepositoriesResponseMocked1 = new GithubApiRepositoriesResponseRecord("test-repo-name-1", false);
        GithubApiRepositoriesResponseRecord githubApiRepositoriesResponseMocked3 = new GithubApiRepositoriesResponseRecord("test-repo-name-3", false);

        //Setting up mocked branches in repos
        GithubApiBranchResponseRecord githubApiBranchResponseFluxMocked1_1 = new GithubApiBranchResponseRecord("test-branch-name-1-1", new GithubApiBranchResponseRecord.Commit("732tr8o2gg87f987"));
        GithubApiBranchResponseRecord githubApiBranchResponseFluxMocked1_2 = new GithubApiBranchResponseRecord("test-branch-name-1-2", new GithubApiBranchResponseRecord.Commit("df444431gg84fgf7"));

        GithubApiBranchResponseRecord githubApiBranchResponseFluxMocked3_1 = new GithubApiBranchResponseRecord("test-branch-name-3-1", new GithubApiBranchResponseRecord.Commit("4543523526dgd254"));

        //Setting up response
        repositoryResponseDTO1 = RepositoryResponseDTO
                .builder()
                .repositoryName(githubApiRepositoriesResponseMocked1.name())
                .ownerLogin(TEST_USERNAME)
                .branches(
                        Stream.of(githubApiBranchResponseFluxMocked1_1, githubApiBranchResponseFluxMocked1_2)
                                .map(branchResponse -> RepositoryResponseDTO.Branch.builder()
                                        .name(branchResponse.name())
                                        .lastCommitSha(branchResponse.commit().sha())
                                        .build())
                                .toList()
                )
                .build();

        repositoryResponseDTO2 = RepositoryResponseDTO
                .builder()
                .repositoryName(githubApiRepositoriesResponseMocked3.name())
                .ownerLogin(TEST_USERNAME)
                .branches(
                        Stream.of(githubApiBranchResponseFluxMocked3_1)
                                .map(branchResponse -> RepositoryResponseDTO.Branch.builder()
                                        .name(branchResponse.name())
                                        .lastCommitSha(branchResponse.commit().sha())
                                        .build())
                                .toList()
                )
                .build();
    }

    @Test
    void testGetUserNotForkedRepositoriesInformationSuccess() {
        //given
        Flux<RepositoryResponseDTO> resposnseFlux = Flux.fromStream(Stream.of(repositoryResponseDTO1, repositoryResponseDTO2));

        //when
        when(githubService.getUserNotForkedRepositoriesInformation(TEST_USERNAME)).thenReturn(resposnseFlux);

        //then
        webTestClient
                .get()
                .uri(CONTROLLER_BASE_URL + "/" + TEST_USERNAME)
                .header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON_VALUE)
                .exchange()
                .expectStatus().isOk()
                .expectBody()

                .jsonPath("$.status").isEqualTo("success")
                .jsonPath("$.results").isArray()
                .jsonPath("$.results[0].repositoryName").isEqualTo(repositoryResponseDTO1.getRepositoryName())
                .jsonPath("$.results[0].ownerLogin").isEqualTo(repositoryResponseDTO1.getOwnerLogin())
                .jsonPath("$.results[0].branches").isArray()
                .jsonPath("$.results[0].branches[0].name").isEqualTo(repositoryResponseDTO1.getBranches().get(0).getName())
                .jsonPath("$.results[0].branches[0].lastCommitSha").isEqualTo(repositoryResponseDTO1.getBranches().get(0).getLastCommitSha())
                .jsonPath("$.results[0].branches[1].name").isEqualTo(repositoryResponseDTO1.getBranches().get(1).getName())
                .jsonPath("$.results[0].branches[1].lastCommitSha").isEqualTo(repositoryResponseDTO1.getBranches().get(1).getLastCommitSha())

                .jsonPath("$.results[1].repositoryName").isEqualTo(repositoryResponseDTO2.getRepositoryName())
                .jsonPath("$.results[1].ownerLogin").isEqualTo(repositoryResponseDTO2.getOwnerLogin())
                .jsonPath("$.results[1].branches").isArray()
                .jsonPath("$.results[1].branches[0].name").isEqualTo(repositoryResponseDTO2.getBranches().get(0).getName())
                .jsonPath("$.results[1].branches[0].lastCommitSha").isEqualTo(repositoryResponseDTO2.getBranches().get(0).getLastCommitSha())
        ;
    }

    @Test
    void testGetUserNotForkedRepositoriesInformationErrorBecauseUserNotFound() {
        //when
        when(githubService.getUserNotForkedRepositoriesInformation(TEST_USERNAME)).thenReturn(Flux.error(new GithubUserNotFoundException("User not found")));

        //then
        webTestClient
                .get()
                .uri(CONTROLLER_BASE_URL + "/" + TEST_USERNAME)
                .header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON_VALUE)
                .exchange()
                .expectStatus().is4xxClientError()
                .expectBody()
                .jsonPath("$.status").isEqualTo("error")
                .jsonPath("$.errors").isArray()
                .jsonPath("$.errors[0].field").isEqualTo("username")
                .jsonPath("$.errors[0].errorMessage").isEqualTo("User not found");
    }



}