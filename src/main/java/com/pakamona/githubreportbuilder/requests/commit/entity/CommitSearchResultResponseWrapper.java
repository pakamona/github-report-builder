package com.pakamona.githubreportbuilder.requests.commit.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class CommitSearchResultResponseWrapper {
    List<CommitSearchResultResponse> items;
}
