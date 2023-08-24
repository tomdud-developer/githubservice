package com.tomdud.githubservice.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class GithubRepositoryResponseDTO {

    private String name;
    private String ownerLogin;
    private List<Branch> branches;

    @Getter
    @Builder
    public static class Branch {
        private String name;
        private String sha;
    }

}


