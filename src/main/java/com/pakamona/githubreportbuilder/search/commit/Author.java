package com.pakamona.githubreportbuilder.search.commit;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Author {
    private String name;
    private String email;
    private String date;
}
