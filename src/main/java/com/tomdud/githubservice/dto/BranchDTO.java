package com.tomdud.githubservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

public class BranchDTO {

    String name;
    String lastCommitSha;

    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty("commit")
    private void unpackNestedCommit(Map<String,Object> commit) {
        this.lastCommitSha = (String)commit.get("sha");
    }

    public String getName() {
        return name;
    }

    public String getLastCommitSha() {
        return lastCommitSha;
    }
}
