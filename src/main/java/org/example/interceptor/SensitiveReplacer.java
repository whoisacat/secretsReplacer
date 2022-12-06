package org.example.interceptor;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpResponse;

import java.util.*;

public interface SensitiveReplacer {

    String PLACEHOLDER = "*****";
    List<String> sensitiveHeaderFields = Arrays.asList("Server", "Connection");
    List<String>  sensitiveBodyFields = Arrays.asList("password", "email", "headers", "data.password", "data", "json.password", "json.username");

    String replaceSensitive(String body, MediaType mediaType);

    default HttpHeaders replaceFromHeaders(ClientHttpResponse response) {
        HttpHeaders headers = new HttpHeaders();
        headers.putAll(response.getHeaders());
        for(String field : sensitiveHeaderFields) {
            headers.replace(field, Collections.singletonList("*****"));
        }
        return headers;
    }
}
