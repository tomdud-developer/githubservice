package com.tomdud.githubservice.service;

import com.tomdud.githubservice.dto.BranchDTO;
import com.tomdud.githubservice.dto.RepositoryDTO;
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

    public GithubService(
            @Value("${token:}") String token,
            @Value("${webclient.api.github.url}") String url,
            @Value("${webclient.api.github.version}") String version
    ) {
        if (!token.isEmpty()) {
            log.info("GithubService::Constructor token mode enabled");
            this.webClient = WebClient.builder()
                    .baseUrl(url)
                    .defaultHeader("Authorization", token)
                    .defaultHeader("X-GitHub-Api-Version", version)
                    .defaultHeader("Accept", "application/vnd.github+json")
                    .build();
        } else {
            log.info("GithubService::Constructor token mode disabled");
            this.webClient = WebClient.builder()
                    .baseUrl(url)
                    .defaultHeader("X-GitHub-Api-Version", version)
                    .defaultHeader("Accept", "application/vnd.github+json")
                    .build();
        }
    }

    public Flux<RepositoryDTO> getUserRepositories(String username) {
        String usersResourceUri = String.format("/users/%s/repos", username);

        log.info("GithubService::getUserRepositories for username {} - send request to GitHub API {}", username, usersResourceUri);

        return webClient.get()
                .uri(usersResourceUri)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, clientErrorResponse -> {
                    if (clientErrorResponse.statusCode().equals(HttpStatus.NOT_FOUND)) {
                        log.error("GithubService::getUserRepositories Username with name {} not found on GitHub", username);
                        return Mono.error(new GithubUserNotFoundException(String.format("Username with name %s not found on GitHub", username)));
                    } else {
                        log.error("GithubService::getUserRepositories GithubBadRequestException, probably reach limit of requests");
                        return Mono.error(new GithubBadRequestException("GithubBadRequestException, you probably reach limit of requests"));
                    }
                })
                .onStatus(HttpStatusCode::isError, clientErrorResponse -> {
                    log.error("GithubService::getUserRepositories GithubApi exception, status code from Github - {}", clientErrorResponse.statusCode().value());
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
                "GithubService::getInformationAboutBranchesInRepository for username {} and repository {} - send request to GitHub API {}",
                username, repositoryName, reposResourceUri
        );

        return webClient.get()
                .uri(reposResourceUri)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, clientErrorResponse -> {
                    if (clientErrorResponse.statusCode().equals(HttpStatus.NOT_FOUND)) {
                        log.error(
                                "GithubService::getInformationAboutBranchesInRepository Username {} or repository {} not found on GitHub",
                                username, repositoryName
                        );
                        return Mono.error(
                                new GithubResourceNotFoundException(
                                        String.format("Username %s or repository %s not found on GitHub", username, repositoryName))
                        );
                    } else {
                        log.error("GithubService::getInformationAboutBranchesInRepository GithubBadRequestException, probably reach limit of requests");
                        return Mono.error(new GithubBadRequestException("GithubBadRequestException, you probably reach limit of requests"));
                    }
                })
                .onStatus(HttpStatusCode::isError, clientErrorResponse -> {
                    log.error("GithubService::getInformationAboutBranchesInRepository GithubApi exception, status code from Github - {}", clientErrorResponse.statusCode().value());
                    return Mono.error(new UnknownGithubApiException(String.format("Unknown GithubApi exception, status code from Github - %d", clientErrorResponse.statusCode().value())));
                })
                .bodyToFlux(BranchDTO.class);
    }

}
