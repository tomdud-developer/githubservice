package com.tomdud.githubservice.service;

import com.tomdud.githubservice.dto.RepositoryResponseDTO;
import com.tomdud.githubservice.dto.githubapi.GithubApiBranchResponseRecord;
import com.tomdud.githubservice.dto.githubapi.GithubApiRepositoriesResponseRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.function.Predicate;

@Service
public class GithubService {

    private final GithubWebClient githubWebClient;
    private final Logger log = LoggerFactory.getLogger(GithubService.class);

    @Autowired
    public GithubService(GithubWebClient githubWebClient) {
        this.githubWebClient = githubWebClient;
    }

    public Flux<RepositoryResponseDTO> getUserNotForkedRepositoriesInformation (String username) {
        log.info("GithubService::getUserNotForkedRepositoriesInformation for username {}", username);

        Flux<GithubApiRepositoriesResponseRecord> repositoriesFlux = githubWebClient.getUserRepositories(username);

        return repositoriesFlux
                .filter(Predicate.not(GithubApiRepositoriesResponseRecord::fork))
                .flatMapSequential(repository -> generateRepositoryResponseDTO(repository, username)
        );
    }

    private Mono<RepositoryResponseDTO> generateRepositoryResponseDTO(GithubApiRepositoriesResponseRecord githubApiRepositoriesResponseRecord, String username) {
        Flux<GithubApiBranchResponseRecord> githubApiBranchResponseDTOFlux =
                getInformationAboutBranchesInRepository(username, githubApiRepositoriesResponseRecord.name());

        return githubApiBranchResponseDTOFlux.collectList().map(
                branches -> RepositoryResponseDTO.builder()
                        .name(githubApiRepositoriesResponseRecord.name())
                        .ownerLogin(username)
                        .branches(generateBranchList(branches))
                        .build()
        );
    }

    private Flux<GithubApiBranchResponseRecord> getInformationAboutBranchesInRepository(String username, String repositoryName) {
        return githubWebClient.getInformationAboutBranchesInRepository(
                username,
                repositoryName
        );
    }

    private List<RepositoryResponseDTO.Branch> generateBranchList(List<GithubApiBranchResponseRecord> githubApiBranchResponseRecordList) {
        return githubApiBranchResponseRecordList.stream().map(branch ->
                RepositoryResponseDTO.Branch
                        .builder()
                        .name(branch.name())
                        .sha(branch.commit().sha())
                        .build()
        ).toList();
    }
}
