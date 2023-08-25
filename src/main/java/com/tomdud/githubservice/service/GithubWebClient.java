package com.tomdud.githubservice.service;

import com.tomdud.githubservice.dto.githubapi.GithubApiRepositoriesResponseRecord;
import com.tomdud.githubservice.dto.githubapi.GithubApiBranchResponseRecord;
import com.tomdud.githubservice.exception.GithubBadRequestException;
import com.tomdud.githubservice.exception.GithubResourceNotFoundException;
import com.tomdud.githubservice.exception.UnknownGithubApiException;
import com.tomdud.githubservice.exception.GithubUserNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import org.springframework.http.HttpStatusCode;

@Service
public class GithubWebClient {

    private final WebClient webClient;
    private final Logger log = LoggerFactory.getLogger(GithubWebClient.class);

    public GithubWebClient (
            WebClient.Builder webClientBuilder,
            @Value("${webclient.api.github.url}") String url,
            @Value("${webclient.api.github.version}") String version,
            @Value("personal-github-token") String personalGithubToken
    ) {
        if (personalGithubToken != null) {
            log.info("GithubWebClient::constructor running with token mode");
            this.webClient = webClientBuilder
                    .baseUrl(url)
                    .defaultHeader("Authorization", personalGithubToken)
                    .defaultHeader("X-GitHub-Api-Version", version)
                    .build();
        } else {
            log.info("GithubWebClient::constructor running without token mode");
            this.webClient = webClientBuilder
                    .baseUrl(url)
                    .defaultHeader("X-GitHub-Api-Version", version)
                    .build();
        }
    }

    Flux<GithubApiRepositoriesResponseRecord> getUserRepositories(String username) {
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
                .bodyToFlux(GithubApiRepositoriesResponseRecord.class);
    }

    Flux<GithubApiBranchResponseRecord> getInformationAboutBranchesInRepository(String username, String repositoryName) {
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
                .bodyToFlux(GithubApiBranchResponseRecord.class);
    }

}
