package com.pakamona.githubreportbuilder.search.code;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class CodeSearchResult {
    private List<CodeSearchFileData> items;
}
