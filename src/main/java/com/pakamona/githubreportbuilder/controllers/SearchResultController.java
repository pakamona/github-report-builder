package com.pakamona.githubreportbuilder.controllers;

import com.google.gson.Gson;
import com.pakamona.githubreportbuilder.client.CodeSearchHttpClient;
import com.pakamona.githubreportbuilder.client.CommitSearchHttpClient;
import com.pakamona.githubreportbuilder.search.code.CodeSearchResult;
import com.pakamona.githubreportbuilder.search.commit.Commit;
import com.pakamona.githubreportbuilder.search.commit.CommitSearchResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@RestController
public class SearchResultController {

    private String datePattern = "YYYY-MM-DD'T'HH:MM:SS'Z'";

    @Autowired
    private CodeSearchHttpClient codeSearchHttpClient;

    @Autowired
    private CommitSearchHttpClient commitSearchHttpClient;

    @Value("${filter.dateFrom}")
    private String filterDateFrom;

    @GetMapping(value = "/execute",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getResults() throws ParseException {
        Gson gson = new Gson();

        Date filterDate = format(filterDateFrom);

        String codeExecuteResults = codeSearchHttpClient.execute("");

        CodeSearchResult codeSearchResult = gson.fromJson(codeExecuteResults, CodeSearchResult.class);

        codeSearchResult.getItems().forEach(i -> {
            String commitSearchResult = commitSearchHttpClient.execute(i.getPath());

            CommitSearchResult[] commitSearchResults = gson.fromJson(commitSearchResult, CommitSearchResult[].class);

            Commit commit = commitSearchResults[0].getCommit();

            String date = commit.getAuthor().getDate();

            try {
                Date commitDate = format(date);

                if (commitDate.after(filterDate)) {
                    System.out.println(commit);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        });

        return ResponseEntity.ok("Success");
    }

    private Date format(String value) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(datePattern);

        return simpleDateFormat.parse(value);
    }
}
