package com.pakamona.githubreportbuilder.core.controllers;

import com.google.gson.Gson;
import com.pakamona.githubreportbuilder.requests.code.client.CodeSearchHttpClient;
import com.pakamona.githubreportbuilder.requests.commit.client.CommitSearchHttpClient;
import com.pakamona.githubreportbuilder.requests.code.entity.CodeSearchResult;
import com.pakamona.githubreportbuilder.requests.commit.entity.CommitSearchResultResponse;
import com.pakamona.githubreportbuilder.requests.commit.entity.CommitSearchResultResponseWrapper;
import com.pakamona.githubreportbuilder.requests.commit.entity.GithubApiCommitResponse;
import com.pakamona.githubreportbuilder.requests.commit.entity.GithubApiCommitSearchResultResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Stream;

@Slf4j
@RestController
public class SearchResultController {

    private CodeSearchHttpClient codeSearchHttpClient;
    private CommitSearchHttpClient commitSearchHttpClient;

    @Autowired
    public SearchResultController(CodeSearchHttpClient codeSearchHttpClient,
                                  CommitSearchHttpClient commitSearchHttpClient) {
        this.codeSearchHttpClient = codeSearchHttpClient;
        this.commitSearchHttpClient = commitSearchHttpClient;
    }

    @Value("${filter.fileNames}")
    private String fileNames;

    @Value("${filter.dateFormat}")
    private String datePattern;

    @GetMapping(value = "/execute",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CommitSearchResultResponseWrapper> getResults(@RequestParam("dateFrom") String filterDateFrom) throws Exception {
        Gson gson = new Gson();

        Set<String> pathsToCommitQuery = new HashSet<>();
        List<CommitSearchResultResponse> commitSearchResultResponse = new ArrayList<>();

        Stream.of(fileNames.split("\\|")).forEach(fileName -> {
            int searchResultPage = 1;

            String[] params = prepareCodeQueryParams(fileName, searchResultPage);

            CodeSearchResult codeSearchResult = gson.fromJson(codeSearchHttpClient.execute(params), CodeSearchResult.class);

            prepareCommitRequestPayload(codeSearchResult, pathsToCommitQuery);

            int totalCount = codeSearchResult.getTotal_count();
            int perPage = 100;

            if (totalCount > perPage) {
                int codeQueryIterations = calculateCodeQueryIterations(totalCount, perPage) + 1; //+1 because one iteration is already done.

                while (searchResultPage != codeQueryIterations) {
                    params = prepareCodeQueryParams(fileName, searchResultPage + 1); //+1 because one iteration is already done.
                    codeSearchResult = gson.fromJson(codeSearchHttpClient.execute(params), CodeSearchResult.class);
                    prepareCommitRequestPayload(codeSearchResult, pathsToCommitQuery);
                    searchResultPage++;
                }
            }
        });

        Date filterDate = format(filterDateFrom);

        pathsToCommitQuery.forEach(path -> {
            if (path.endsWith("_cq_dialog/.content.xml") || path.endsWith("_cq_dialog.xml") || path.endsWith("dialog.xml")) { //todo replace this hard code
                String commitSearchResult = commitSearchHttpClient.execute(new String[]{path});

                GithubApiCommitSearchResultResponse[] commitSearchResults
                        = gson.fromJson(commitSearchResult, GithubApiCommitSearchResultResponse[].class);
                GithubApiCommitResponse lastCommit = commitSearchResults[0].getCommit(); // get latest commit
                String lastCommitDate = lastCommit.getAuthor().getDate();

                try {
                    Date commitDate = format(lastCommitDate);

                    if (commitDate.after(filterDate)) {
                        commitSearchResultResponse.add(
                                new CommitSearchResultResponse(path, lastCommit.getAuthor().getName(),
                                        lastCommitDate, lastCommit.getMessage())
                        );
                    }
                } catch (ParseException e) {
                    log.error("Unable to cast date [{}] by pattern [{}]", lastCommitDate, datePattern);
                }
            }
        });

        return ResponseEntity.ok(new CommitSearchResultResponseWrapper(commitSearchResultResponse));
    }

    private Date format(String value) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(datePattern);

        return simpleDateFormat.parse(value);
    }

    private int calculateCodeQueryIterations(int itemsCount, int perPage) {
        return itemsCount / perPage;
    }

    private String[] prepareCodeQueryParams(String fileName, int searchResultPage) {
        return new String[]{fileName, String.valueOf(searchResultPage)};
    }

    private void prepareCommitRequestPayload(CodeSearchResult codeSearchResult, Set<String> pathsToCommitQuery) {
        codeSearchResult.getItems().forEach(item -> pathsToCommitQuery.add(item.getPath()));
    }
}
