package com.tomdud.githubservice.service;

import com.tomdud.githubservice.dto.githubapi.GithubApiRepositoriesResponseRecord;
import com.tomdud.githubservice.dto.githubapi.GithubApiBranchResponseRecord;
import com.tomdud.githubservice.exception.GithubUserNotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
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
            @Value("${webclient.api.github.version}") String version
    ) {
        this.webClient = webClientBuilder
                .baseUrl(url)
                .defaultHeader("X-GitHub-Api-Version", version)
                .build();
    }

    Flux<GithubApiRepositoriesResponseRecord> getUserRepositories(String username) {
        String usersResourceUri = String.format("/users/%s/repos", username);

        log.info("GithubWebClient::getUserRepositories for username {} - send request to GitHub API {}", username, usersResourceUri);

        return webClient.get()
                .uri(usersResourceUri)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, clientResponse -> {
                    log.error("GithubWebClient::getUserRepositories Username with name {} not found on GitHub", username);
                    return Mono.error(new GithubUserNotFoundException(String.format("Username with name %s not found on GitHub", username)));
                })
                .onStatus(HttpStatusCode::isError, clientResponse -> {
                    return clientResponse.createException();
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
                .onStatus(HttpStatusCode::is4xxClientError, clientResponse -> {
                    log.error(
                            "GithubWebClient::getInformationAboutBranchesInRepository Username with name {} or repository with name {} not found on GitHub",
                            username, repositoryName
                    );
                    return Mono.error(new GithubUserNotFoundException(String.format("Username with name %s not found on GitHub", username)));
                })
                .bodyToFlux(GithubApiBranchResponseRecord.class);
    }

}
