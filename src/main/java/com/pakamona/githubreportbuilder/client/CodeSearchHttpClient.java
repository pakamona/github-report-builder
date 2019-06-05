package com.pakamona.githubreportbuilder.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class CodeSearchHttpClient extends AbstractHttpClient {

    @Value("${search.code}")
    private String query;

    @Override
    public String getQuery() {
        return query;
    }
}
