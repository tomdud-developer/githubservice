package com.tomdud.githubservice.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

public class RepositoryDTO {

    @JsonAlias({"name"})
    private String repositoryName;
    private String repositoryOwner;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private boolean fork;
    private List<BranchDTO> branches;

    public void setRepositoryName(String repositoryName) {
        this.repositoryName = repositoryName;
    }

    public void setRepositoryOwner(String repositoryOwner) {
        this.repositoryOwner = repositoryOwner;
    }

    @JsonProperty("owner")
    private void unpackNestedOwner(Map<String,Object> owner) {
        this.repositoryOwner = (String)owner.get("login");
    }

    public void setFork(boolean fork) {
        this.fork = fork;
    }

    public void setBranches(List<BranchDTO> branches) {
        this.branches = branches;
    }

    public String getRepositoryName() {
        return repositoryName;
    }

    public boolean isFork() {
        return fork;
    }

    public List<BranchDTO> getBranches() {
        return branches;
    }

    public String getRepositoryOwner() {
        return repositoryOwner;
    }
}
