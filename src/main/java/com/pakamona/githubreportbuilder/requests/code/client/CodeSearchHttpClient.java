package com.pakamona.githubreportbuilder.requests.code.client;

import com.pakamona.githubreportbuilder.core.client.AbstractHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class CodeSearchHttpClient extends AbstractHttpClient {

    @Value("${search.code}")
    private String query;

    @Override
    public String prepareQuery(String[] params) {
        String fileNameMask = "{fileName}";
        String searchResultPage = "{searchResultPage}";

        return query.replace(fileNameMask, params[0]).replace(searchResultPage, params[1]);
    }
}
