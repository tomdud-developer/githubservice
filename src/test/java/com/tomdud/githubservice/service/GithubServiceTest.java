package com.tomdud.githubservice.service;

import com.tomdud.githubservice.dto.RepositoryResponseDTO;
import com.tomdud.githubservice.dto.githubapi.GithubApiBranchResponseRecord;
import com.tomdud.githubservice.dto.githubapi.GithubApiRepositoriesResponseRecord;
import com.tomdud.githubservice.exception.GithubUserNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;
import java.util.stream.Stream;
import static org.mockito.Mockito.when;

@SpringBootTest
class GithubServiceTest {

    @Autowired
    private GithubService githubService;

    @MockBean
    private GithubWebClient githubWebClient;

    private final String TEST_USERNAME = "test-username";

    //Declaring mocked responses
    private GithubApiRepositoriesResponseRecord githubApiRepositoriesResponseMocked1;
    private GithubApiRepositoriesResponseRecord githubApiRepositoriesResponseMocked2;
    private GithubApiRepositoriesResponseRecord githubApiRepositoriesResponseMocked3;
    private GithubApiRepositoriesResponseRecord githubApiRepositoriesResponseMocked4;
    private Flux<GithubApiRepositoriesResponseRecord> githubApiRepositoriesResponseRecordFluxMocked;
    private Flux<GithubApiBranchResponseRecord> githubApiBranchResponseFluxMocked1;
    private Flux<GithubApiBranchResponseRecord> githubApiBranchResponseFluxMocked2;
    private Flux<GithubApiBranchResponseRecord> githubApiBranchResponseFluxMocked3;
    private Flux<GithubApiBranchResponseRecord> githubApiBranchResponseFluxMocked4;
    private RepositoryResponseDTO repositoryResponseDTO1;
    private RepositoryResponseDTO repositoryResponseDTO3;

    @BeforeEach
    void setUp() {
        //Setting up mocked repos
        githubApiRepositoriesResponseMocked1 = new GithubApiRepositoriesResponseRecord("test-repo-name-1", false);
        githubApiRepositoriesResponseMocked2 = new GithubApiRepositoriesResponseRecord("test-repo-name-2", true);
        githubApiRepositoriesResponseMocked3 = new GithubApiRepositoriesResponseRecord("test-repo-name-3", false);
        githubApiRepositoriesResponseMocked4 = new GithubApiRepositoriesResponseRecord("test-repo-name-4", true);

        githubApiRepositoriesResponseRecordFluxMocked =
                Flux.fromStream(
                        Stream.of(
                                githubApiRepositoriesResponseMocked1,
                                githubApiRepositoriesResponseMocked2,
                                githubApiRepositoriesResponseMocked3,
                                githubApiRepositoriesResponseMocked4)
                );

        //Setting up mocked branches in repos
        GithubApiBranchResponseRecord githubApiBranchResponseFluxMocked1_1 = new GithubApiBranchResponseRecord("test-branch-name-1-1", new GithubApiBranchResponseRecord.Commit("732tr8o2gg87f987"));
        GithubApiBranchResponseRecord githubApiBranchResponseFluxMocked1_2 = new GithubApiBranchResponseRecord("test-branch-name-1-2", new GithubApiBranchResponseRecord.Commit("df444431gg84fgf7"));

        GithubApiBranchResponseRecord githubApiBranchResponseFluxMocked2_1 = new GithubApiBranchResponseRecord("test-branch-name-2-1", new GithubApiBranchResponseRecord.Commit("423i423io4hh435j"));
        GithubApiBranchResponseRecord githubApiBranchResponseFluxMocked2_2 = new GithubApiBranchResponseRecord("test-branch-name-2-2", new GithubApiBranchResponseRecord.Commit("3434h3u2h42u3523"));

        GithubApiBranchResponseRecord githubApiBranchResponseFluxMocked3_1 = new GithubApiBranchResponseRecord("test-branch-name-3-1", new GithubApiBranchResponseRecord.Commit("4543523526dgd254"));

        GithubApiBranchResponseRecord githubApiBranchResponseFluxMocked4_1 = new GithubApiBranchResponseRecord("test-branch-name-4-1", new GithubApiBranchResponseRecord.Commit("67567gdg245233f4"));
        GithubApiBranchResponseRecord githubApiBranchResponseFluxMocked4_2 = new GithubApiBranchResponseRecord("test-branch-name-4-2", new GithubApiBranchResponseRecord.Commit("f4f43t4534543543"));
        GithubApiBranchResponseRecord githubApiBranchResponseFluxMocked4_3 = new GithubApiBranchResponseRecord("test-branch-name-4-3", new GithubApiBranchResponseRecord.Commit("fdf43534543f3434"));


        githubApiBranchResponseFluxMocked1 = Flux.fromStream(Stream.of(githubApiBranchResponseFluxMocked1_1, githubApiBranchResponseFluxMocked1_2));
        githubApiBranchResponseFluxMocked2 = Flux.fromStream(Stream.of(githubApiBranchResponseFluxMocked2_1, githubApiBranchResponseFluxMocked2_2));
        githubApiBranchResponseFluxMocked3 = Flux.fromStream(Stream.of(githubApiBranchResponseFluxMocked3_1));
        githubApiBranchResponseFluxMocked4 = Flux.fromStream(Stream.of(githubApiBranchResponseFluxMocked4_1, githubApiBranchResponseFluxMocked4_2, githubApiBranchResponseFluxMocked4_3));

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

        repositoryResponseDTO3 = RepositoryResponseDTO
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
        //when
        when(githubWebClient.getUserRepositories(TEST_USERNAME)).thenReturn(githubApiRepositoriesResponseRecordFluxMocked);
        when(githubWebClient.getInformationAboutBranchesInRepository(TEST_USERNAME, githubApiRepositoriesResponseMocked1.name())).thenReturn(githubApiBranchResponseFluxMocked1);
        when(githubWebClient.getInformationAboutBranchesInRepository(TEST_USERNAME, githubApiRepositoriesResponseMocked2.name())).thenReturn(githubApiBranchResponseFluxMocked2);
        when(githubWebClient.getInformationAboutBranchesInRepository(TEST_USERNAME, githubApiRepositoriesResponseMocked3.name())).thenReturn(githubApiBranchResponseFluxMocked3);
        when(githubWebClient.getInformationAboutBranchesInRepository(TEST_USERNAME, githubApiRepositoriesResponseMocked4.name())).thenReturn(githubApiBranchResponseFluxMocked4);

        Flux<RepositoryResponseDTO> repositoryFlux = githubService.getUserNotForkedRepositoriesInformation(TEST_USERNAME);

        //then
        StepVerifier.create(repositoryFlux)
                .expectNext(repositoryResponseDTO1)
                .expectNext(repositoryResponseDTO3)
                .verifyComplete();
    }

    @Test
    void testGetUserNotForkedRepositoriesInformationThrowUserNotFoundException() {
        //when
        when(githubWebClient.getUserRepositories(TEST_USERNAME + "_not_exist")).thenReturn(Flux.error(new GithubUserNotFoundException("User not found")));

        Flux<RepositoryResponseDTO> repositoryFlux = githubService.getUserNotForkedRepositoriesInformation(TEST_USERNAME + "_not_exist");

        //then
        StepVerifier.create(repositoryFlux)
                .expectError(GithubUserNotFoundException.class)
                .verify();
    }


}