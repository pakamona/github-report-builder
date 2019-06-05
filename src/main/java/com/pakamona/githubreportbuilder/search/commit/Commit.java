package com.pakamona.githubreportbuilder.search.commit;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Commit {
    private Author author;
    private String message;
}
