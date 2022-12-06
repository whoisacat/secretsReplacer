package org.example.interceptor;

import org.springframework.http.MediaType;
import java.util.*;
public class XWwwFormUrlencodedSensitiveReplacer implements SensitiveReplacer {
    @Override
    public String replaceSensitive(String body, MediaType mediaType) {
        if (!mediaType.toString().contains(MediaType.APPLICATION_FORM_URLENCODED_VALUE)) {
            return body;
        }
        Map<String, String> bodyMap = new HashMap<>();
        String[] tuples = body.split("&");
        for (String tuple : tuples) {
            String[] splitTuple = tuple.split("=", 2);
            bodyMap.put(splitTuple[0], splitTuple[1]);
        }
        for (String field : sensitiveBodyFields) {
            bodyMap.replace(field, PLACEHOLDER);
        }
        return bodyMap.toString();
    }
}
