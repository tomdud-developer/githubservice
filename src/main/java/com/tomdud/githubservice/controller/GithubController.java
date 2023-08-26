package com.tomdud.githubservice.controller;

import com.tomdud.githubservice.dto.RepositoryDTO;
import com.tomdud.githubservice.service.GithubService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;


@RestController
@RequestMapping("api/v1/github/repositories")
@AllArgsConstructor
@Tag(name = "GitHub controller")
public class GithubController {

    private final Logger log = LoggerFactory.getLogger(GithubController.class);
    private final GithubService githubService;


    @GetMapping(
            value = "/{username}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public Flux<RepositoryDTO> getUserNotForkedRepositoriesInformation(@PathVariable String username) {
        log.info("GithubController::getUserNotForkedRepositoriesInformation::GetMapping - for {}", username);

        return githubService.getUserRepositories(username);
    }

}
