package com.tomdud.githubservice.service;

import com.tomdud.githubservice.dto.GithubRepositoryResponseDTO;
import com.tomdud.githubservice.dto.githubapi.GithubApiBranchResponseRecord;
import com.tomdud.githubservice.dto.githubapi.GithubApiRepositoriesResponseRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.stream.Collectors;

@Service
public class GithubService {

    private final GithubWebClient githubWebClient;

    @Autowired
    public GithubService(GithubWebClient githubWebClient) {
        this.githubWebClient = githubWebClient;
    }

    public Flux<GithubRepositoryResponseDTO> getUserNotForkedRepositoriesInformation (String username) {

        Flux<GithubApiRepositoriesResponseRecord> repositoriesFlux = githubWebClient.getUserRepositories(username);
        return repositoriesFlux.flatMapSequential(
                repository -> {
                    Flux<GithubApiBranchResponseRecord> githubApiBranchResponseDTOFlux =
                            getInformationAboutBranchesInRepository(username, repository.name());

                    return githubApiBranchResponseDTOFlux.collectList().map(
                            branches -> GithubRepositoryResponseDTO.builder()
                                    .name(repository.name())
                                    .ownerLogin(username)
                                    .branches(
                                            branches.stream().map(branch ->
                                                        GithubRepositoryResponseDTO.Branch
                                                                .builder()
                                                                .name(branch.name())
                                                                .sha(branch.commit().sha())
                                                                .build()
                                            ).collect(Collectors.toList())
                                    ).build()
                    );
                }
        );
    }

    private Flux<GithubApiBranchResponseRecord> getInformationAboutBranchesInRepository(String username, String repositoryName) {
        return githubWebClient.getInformationAboutBranchesInRepository(
                    username,
                    repositoryName
        );
    }
}
