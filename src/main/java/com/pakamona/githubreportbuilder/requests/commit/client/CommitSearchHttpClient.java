package com.pakamona.githubreportbuilder.requests.commit.client;

import com.pakamona.githubreportbuilder.core.client.AbstractHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class CommitSearchHttpClient extends AbstractHttpClient {

    @Value("${search.commit}")
    private String query;

    @Override
    public String getQuery() {
        return query;
    }
}
