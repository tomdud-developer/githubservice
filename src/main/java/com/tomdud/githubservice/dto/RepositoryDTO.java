package com.tomdud.githubservice.dto;

import com.fasterxml.jackson.annotation.JsonAlias;

import java.util.List;

public class RepositoryDTO {

    @JsonAlias({"name"})
    private String repositoryName;
    private boolean fork;
    private List<BranchDTO> branches;


    public String getRepositoryName() {
        return repositoryName;
    }

    public void setRepositoryName(String repositoryName) {
        this.repositoryName = repositoryName;
    }

    public boolean isFork() {
        return fork;
    }

    public void setFork(boolean fork) {
        this.fork = fork;
    }

    public List<BranchDTO> getBranches() {
        return branches;
    }

    public void setBranches(List<BranchDTO> branches) {
        this.branches = branches;
    }
}
