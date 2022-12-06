package org.example.interceptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class RequestResponseLoggingInterceptor implements ClientHttpRequestInterceptor {

    private final List<SensitiveReplacer> replacers;

    public RequestResponseLoggingInterceptor(List<SensitiveReplacer> replacers) {
        this.replacers = replacers;
    }
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException
    {
        logRequest(request, body);
        ClientHttpResponse response = execution.execute(request, body);
        logResponse(response);
        response.getHeaders().add("headerName", "VALUE");

        return response;
    }

    private void logRequest(HttpRequest request, byte[] body) throws IOException
    {
        if (log.isDebugEnabled())
        {
            log.debug("===========================request begin================================================");
            log.debug("URI         : {}", request.getURI());
            log.debug("Method      : {}", request.getMethod());
            log.debug("Headers     : {}", request.getHeaders());
            String bodyString = new String(body, "UTF-8");
            for (SensitiveReplacer r : replacers) {
                bodyString = r.replaceSensitive(bodyString, request.getHeaders().getContentType());
            }
            log.debug("Request body: {}", bodyString);
            log.debug("==========================request end================================================");
        }
    }

    private void logResponse(ClientHttpResponse response) throws IOException
    {
        if (log.isDebugEnabled())
        {
            log.debug("============================response begin==========================================");
            log.debug("Status code  : {}", response.getStatusCode());
            log.debug("Status text  : {}", response.getStatusText());
            AtomicReference<HttpHeaders> h = new AtomicReference<>(response.getHeaders());
            replacers.stream().findAny().ifPresent(it -> h.set(it.replaceFromHeaders(response)));
            log.debug("Headers      : {}", h.get());
            String bodyString = StreamUtils.copyToString(response.getBody(), StandardCharsets.UTF_8);
            for (SensitiveReplacer r : replacers) {
                bodyString = r.replaceSensitive(bodyString, response.getHeaders().getContentType());
            }
            log.debug("Responce body: {}", bodyString.replace("\n", ""));
            log.debug("=======================response end=================================================");
        }
    }
}
