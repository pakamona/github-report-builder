package com.pakamona.githubreportbuilder.core.client;

import org.apache.commons.codec.CharEncoding;
import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeader;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;

public abstract class AbstractHttpClient {

    @Value("${authorization.header.name}")
    private String authHeaderName;

    @Value("${authorization.header.value}")
    private String authHeaderValue;

    @Value("${connection.timeout}")
    private int connectionTimeout;

    @Value("${connection.requestTimeout}")
    private int requestTimeout;

    @Value("${connection.socketTimeout}")
    private int socketTimeout;

    @Value("${connection.maxConnectionPool}")
    private int maxConnectionPool;

    @Value("${connection.maxConnectionPerRoute}")
    private int maxConnectionPerRoute;

    private CloseableHttpClient client;

    public String execute(String[] params) {
        HttpGet httpGet = new HttpGet(getQuery(params));
        try (CloseableHttpResponse response = client.execute(httpGet); InputStream content = response.getEntity().getContent()) {
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == 200) {
                return IOUtils.toString(content, CharEncoding.UTF_8);
            } else {
                throw new IllegalStateException("Can't process request, status=" + statusCode);
            }
        } catch (IOException e) {
            throw new IllegalStateException("Can't process request", e);
        }
    }

    public abstract String prepareQuery(String[] params);

    private String getQuery(String[] params) {
        return prepareQuery(params);
    }

    @PostConstruct
    private void init() {
        HttpClientBuilder builder = HttpClientBuilder.create();

        Header authorizationHeader = new BasicHeader(authHeaderName, authHeaderValue);
        builder.setDefaultHeaders(Collections.singletonList(authorizationHeader));

        PoolingHttpClientConnectionManager poolingConnectionManager = configureConnectionManager(maxConnectionPool, maxConnectionPerRoute);
        builder.setConnectionManager(poolingConnectionManager);
        builder.setDefaultRequestConfig(configureRequest(connectionTimeout, requestTimeout, socketTimeout));

        this.client = builder.build();
    }

    private static PoolingHttpClientConnectionManager configureConnectionManager(final int maxConnectionsInPool, final int maxConnectionsPerRoute) {
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
        connectionManager.setMaxTotal(maxConnectionsInPool);
        connectionManager.setDefaultMaxPerRoute(maxConnectionsPerRoute);
        return connectionManager;
    }

    private static RequestConfig configureRequest(final int connectionTimeout, final int connectionRequestTimeout, final int socketTimeout) {
        return RequestConfig.custom()
                .setConnectTimeout(connectionTimeout)
                .setConnectionRequestTimeout(connectionRequestTimeout)
                .setSocketTimeout(socketTimeout)
                .build();
    }
}
