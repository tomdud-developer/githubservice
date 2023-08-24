package com.tomdud.githubservice.service;

import com.tomdud.githubservice.dto.githubapi.GithubApiRepositoriesResponseDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

@Service
public class GithubWebClient {

    @Value("webclient.api.github.url")
    private String URL;

    @Value("webclient.api.github.version")
    private String VERSION;

    private final WebClient webClient;

    public GithubWebClient() {
        webClient = WebClient.builder()
                .baseUrl(URL)
                .defaultHeader("X-GitHub-Api-Version", VERSION)
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
