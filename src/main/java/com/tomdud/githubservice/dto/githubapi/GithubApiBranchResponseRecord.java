package com.tomdud.githubservice.dto.githubapi;

public record GithubApiBranchResponseRecord(
        String name,
        GithubApiBranchResponseRecord.Commit commit
) {
    public record Commit(String sha) {}
}

