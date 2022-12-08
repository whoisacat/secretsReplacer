package org.example.interceptor;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.util.*;

public interface SensitiveReplacer {

    String PLACEHOLDER = "*****";

    String replaceSensitive(String body, MediaType mediaType);

    default HttpHeaders replaceFromHeaders(HttpHeaders responseHeaders) {
        HttpHeaders headers = new HttpHeaders();
        headers.putAll(responseHeaders);
        for(String field : getSensitiveHeaderFields()) {
            headers.replace(field, Collections.singletonList("*****"));
        }
        return headers;
    }

    List<String> getSensitiveHeaderFields();
}
