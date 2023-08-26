package com.tomdud.githubservice.service;

import com.tomdud.githubservice.dto.BranchDTO;
import com.tomdud.githubservice.dto.RepositoryDTO;
import com.tomdud.githubservice.dto.githubapi.GithubApiBranchResponseRecord;
import com.tomdud.githubservice.dto.githubapi.GithubApiRepositoriesResponseRecord;
import com.tomdud.githubservice.exception.GithubBadRequestException;
import com.tomdud.githubservice.exception.GithubResourceNotFoundException;
import com.tomdud.githubservice.exception.GithubUserNotFoundException;
import com.tomdud.githubservice.exception.UnknownGithubApiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.function.Predicate;

@Service
public class GithubService {

    private final Logger log = LoggerFactory.getLogger(GithubService.class);
    private final WebClient webClient;

    @Value("${webclient.api.github.url}")
    private String url;

    @Value("${webclient.api.github.version}")
    private  String version;

    public GithubService() {
        this.webClient = WebClient.builder()
                .baseUrl(url)
                .defaultHeader("X-GitHub-Api-Version", version)
                .defaultHeader("Accept", "application/vnd.github+json")
                .build();
    }


    public Flux<RepositoryDTO> getUserRepositories(String username) {
        String usersResourceUri = String.format("/users/%s/repos", username);

        log.info("GithubWebClient::getUserRepositories for username {} - send request to GitHub API {}", username, usersResourceUri);

        return webClient.get()
                .uri(usersResourceUri)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, clientErrorResponse -> {
                    if (clientErrorResponse.statusCode().equals(HttpStatus.NOT_FOUND)) {
                        log.error("GithubWebClient::getUserRepositories Username with name {} not found on GitHub", username);
                        return Mono.error(new GithubUserNotFoundException(String.format("Username with name %s not found on GitHub", username)));
                    } else {
                        log.error("GithubWebClient::getUserRepositories GithubBadRequestException, probably reach limit of requests");
                        return Mono.error(new GithubBadRequestException("GithubBadRequestException, you probably reach limit of requests"));
                    }
                })
                .onStatus(HttpStatusCode::isError, clientErrorResponse -> {
                    log.error("GithubWebClient::getUserRepositories GithubApi exception, status code from Github - {}", clientErrorResponse.statusCode().value());
                    return Mono.error(new UnknownGithubApiException(String.format("Unknown GithubApi exception, status code from Github - %d", clientErrorResponse.statusCode().value())));
                })
                .bodyToFlux(RepositoryDTO.class)
                .filter(Predicate.not(RepositoryDTO::isFork))
                .flatMap(repository -> {
                    Flux<BranchDTO> branchInfo = getInformationAboutBranchesInRepository(username, repository.getRepositoryName());
                    return branchInfo.collectList().map(branchesList -> {
                        repository.setBranches(branchesList);
                        return repository;
                    });
                });

    }


    private Flux<BranchDTO> getInformationAboutBranchesInRepository(String username, String repositoryName) {
        String reposResourceUri = String.format("/repos/%s/%s/branches", username, repositoryName);

        log.info(
                "GithubWebClient::getInformationAboutBranchesInRepository for username {} and repository {} - send request to GitHub API {}",
                username, repositoryName, reposResourceUri
        );

        return webClient.get()
                .uri(reposResourceUri)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, clientErrorResponse -> {
                    if (clientErrorResponse.statusCode().equals(HttpStatus.NOT_FOUND)) {
                        log.error(
                                "GithubWebClient::getInformationAboutBranchesInRepository Username {} or repository {} not found on GitHub",
                                username, repositoryName
                        );
                        return Mono.error(
                                new GithubResourceNotFoundException(
                                        String.format("Username %s or repository %s not found on GitHub", username, repositoryName))
                        );
                    } else {
                        log.error("GithubWebClient::getInformationAboutBranchesInRepository GithubBadRequestException, probably reach limit of requests");
                        return Mono.error(new GithubBadRequestException("GithubBadRequestException, you probably reach limit of requests"));
                    }
                })
                .onStatus(HttpStatusCode::isError, clientErrorResponse -> {
                    log.error("GithubWebClient::getInformationAboutBranchesInRepository GithubApi exception, status code from Github - {}", clientErrorResponse.statusCode().value());
                    return Mono.error(new UnknownGithubApiException(String.format("Unknown GithubApi exception, status code from Github - %d", clientErrorResponse.statusCode().value())));
                })
                .bodyToFlux(BranchDTO.class);
    }

    /*
    public Flux<RepositoryResponseDTO> getUserNotForkedRepositoriesInformation(String username) {
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
                        .repositoryName(githubApiRepositoriesResponseRecord.name())
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
                        .lastCommitSha(branch.commit().sha())
                        .build()
        ).toList();
    }

    */

}
