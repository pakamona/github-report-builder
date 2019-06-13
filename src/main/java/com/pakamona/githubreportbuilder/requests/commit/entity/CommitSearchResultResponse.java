package com.pakamona.githubreportbuilder.requests.commit.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class CommitSearchResultResponse {
    private String path;
    private String author;
    private String date;
    private String message;
}
