package com.pakamona.githubreportbuilder.requests.commit.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GithubApiCommitResponse {
    private GithubApiAuthorResponse author;
    private String message;
}
