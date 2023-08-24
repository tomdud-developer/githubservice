package com.tomdud.githubservice.service;

import com.tomdud.githubservice.dto.GithubRepositoryResponseDTO;
import com.tomdud.githubservice.dto.githubapi.GithubApiBranchResponseDTO;
import com.tomdud.githubservice.dto.githubapi.GithubApiRepositoriesResponseDTO;
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

        Flux<GithubApiRepositoriesResponseDTO> repositoriesFlux = githubWebClient.getUserRepositories(username);
        return repositoriesFlux.flatMapSequential(
                repository -> {
                    Flux<GithubApiBranchResponseDTO> githubApiBranchResponseDTOFlux =
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
                                                                .sha(branch.sha())
                                                                .build()
                                            ).collect(Collectors.toList())
                                    ).build()
                    );
                }
        );
    }

    private Flux<GithubApiBranchResponseDTO> getInformationAboutBranchesInRepository(String username, String repositoryName) {
        return githubWebClient.getInformationAboutBranchesInRepository(
                    username,
                    repositoryName
        );
    }
}
