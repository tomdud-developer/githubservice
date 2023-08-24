package com.tomdud.githubservice.service;

import com.tomdud.githubservice.dto.githubapi.GithubApiRepositoriesResponseDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

@Service
public class GithubWebClient {

    private final WebClient webClient;

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

    Flux<GithubApiRepositoriesResponseDTO> getUserRepositories(String username) {
        String usersResourceUri = String.format("/users/%s/repos", username);

        return webClient.get()
                .uri(usersResourceUri)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToFlux(GithubApiRepositoriesResponseDTO.class);
    }

}
