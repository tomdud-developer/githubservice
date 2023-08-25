package com.tomdud.githubservice.controller;

import com.tomdud.githubservice.dto.ApiResponse;
import com.tomdud.githubservice.dto.RepositoryResponseDTO;
import com.tomdud.githubservice.service.GithubService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("github/repositories")
@AllArgsConstructor
public class GithubController {

    private final GithubService githubService;

    @GetMapping("/{username}")
    public Mono<ResponseEntity<ApiResponse<List<RepositoryResponseDTO>>>> getUserNotForkedRepositoriesInformation(@PathVariable String username) {
        return githubService.getUserNotForkedRepositoriesInformation(username)
                .collectList()
                .map(response -> ApiResponse.<List<RepositoryResponseDTO>>builder()
                        .status("success")
                        .results(response)
                        .build()
                ).map(ResponseEntity::ok);
    }
}
