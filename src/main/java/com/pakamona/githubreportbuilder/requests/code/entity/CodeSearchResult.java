package com.pakamona.githubreportbuilder.requests.code.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class CodeSearchResult {
    private int total_count;
    private List<CodeSearchFileData> items;
}
