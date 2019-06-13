package com.pakamona.githubreportbuilder.requests.commit.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GithubApiAuthorResponse {
    private String name;
    private String email;
    private String date;
}
