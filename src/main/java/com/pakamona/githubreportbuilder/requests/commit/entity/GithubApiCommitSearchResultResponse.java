package com.pakamona.githubreportbuilder.requests.commit.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GithubApiCommitSearchResultResponse {
    private GithubApiCommitResponse commit;
}
