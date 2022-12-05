package org.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Function;

public class SensitiveReplacer {

    public static final String PLACEHOLDER = "*****";
    public static final String JSON_FILD_SPLITTER_REGEX = "\\.";

    public SensitiveReplacer() {
        replaceFunctions.put(MediaType.APPLICATION_JSON_VALUE, this::replaceFromApplicationJson);
        //todo другие MediaType
    }

    //todo внедрить через DI
    private final List<String> sensitiveHeaderFields = Arrays.asList("Server", "Connection");
    private final List<String>  sensitiveBodyFields = Arrays.asList("headers", "data.password", "data.username", "json.password", "json.username");

    private final Map<String, Function<ClientHttpResponse, String>> replaceFunctions = new HashMap<>();

    public String clearHttpBodyFromSensitive(ClientHttpResponse response) {
        MediaType contentType = response.getHeaders().getContentType();
        if (contentType == null) {
            return "";
        }
        return replaceFunctions.getOrDefault(contentType.toString(), this::replaceDefault).apply(response);
    }

    private String replaceDefault(ClientHttpResponse response) {
        try {
            return StreamUtils.copyToString(response.getBody(), Charset.defaultCharset());
        } catch (IOException | ClassCastException e) {
            e.printStackTrace();
            return "";
        }
    }

    private String replaceFromApplicationJson(ClientHttpResponse response) {
        try {
            String bodyString = StreamUtils.copyToString(response.getBody(), StandardCharsets.UTF_8);
            Map<String, Object> map = new ObjectMapper().readValue(bodyString, HashMap.class);
            for (String field : sensitiveBodyFields) {
                String[] keys = field.split(JSON_FILD_SPLITTER_REGEX);
                Map<String, Object> entrance = map;
                for (String key : keys) {
                    if (Map.class.isAssignableFrom(entrance.get(key).getClass())) {
                        entrance = (Map<String, Object>) entrance.get(key);
                    } else if (String.class.isAssignableFrom(entrance.get(key).getClass())) {
                        entrance.put(key, PLACEHOLDER);
                        break;
                    }
                }
                map.replace(field, PLACEHOLDER);
            }
            return map.toString();
        } catch (IOException | ClassCastException e) {
            e.printStackTrace();
            return "";
        }
    }

    public HttpHeaders clearHttpHeadersFromSensitive(ClientHttpResponse response) {
        HttpHeaders headers = new HttpHeaders();
        headers.putAll(response.getHeaders());
        for(String field : sensitiveHeaderFields) {
            headers.replace(field, Collections.singletonList("*****"));
        }
        return headers;
    }
}
