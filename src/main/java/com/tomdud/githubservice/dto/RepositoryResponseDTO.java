package com.tomdud.githubservice.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class RepositoryResponseDTO {

    private String repositoryName;
    private String ownerLogin;
    private List<Branch> branches;

    @Getter
    @Builder
    public static class Branch {
        private String name;
        private String lastCommitSha;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Branch branch = (Branch) o;

            if (!name.equals(branch.name)) return false;
            return lastCommitSha.equals(branch.lastCommitSha);
        }

        @Override
        public int hashCode() {
            int result = name.hashCode();
            result = 31 * result + lastCommitSha.hashCode();
            return result;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RepositoryResponseDTO that = (RepositoryResponseDTO) o;

        if (!repositoryName.equals(that.repositoryName)) return false;
        if (!ownerLogin.equals(that.ownerLogin)) return false;
        return branches.equals(that.branches);
    }

    @Override
    public int hashCode() {
        int result = repositoryName.hashCode();
        result = 31 * result + ownerLogin.hashCode();
        result = 31 * result + branches.hashCode();
        return result;
    }
}


