package org.example.interceptor;

import org.springframework.http.MediaType;
import java.util.*;

public class XWwwFormUrlencodedSensitiveReplacer implements SensitiveReplacer {

    public static final String X_WWW_FORM_URLENCODED_KEY_VALUE_SPLITTER_REGEX = "=";
    private static final String X_WWW_FORM_URLENCODED_SPLITTER_REGEX = "&";
    private final List<String> sensitiveBodyFields;
    private final List<String> sensitiveHeaderFields;

    @Override
    public List<String> getSensitiveHeaderFields() {
        return sensitiveHeaderFields;
    }

    public XWwwFormUrlencodedSensitiveReplacer(List<String> sensitiveHeaderFields,
                                               List<String> sensitiveBodyFields) {
        this.sensitiveBodyFields = sensitiveBodyFields;
        this.sensitiveHeaderFields = sensitiveHeaderFields;
    }

    @Override
    public String replaceSensitive(String body, MediaType mediaType) {
        if (!mediaType.toString().contains(MediaType.APPLICATION_FORM_URLENCODED_VALUE)) {
            return body;
        }
        Map<String, String> bodyMap = new HashMap<>();
        String[] tuples = body.split(X_WWW_FORM_URLENCODED_SPLITTER_REGEX);
        for (String tuple : tuples) {
            String[] splitTuple = tuple.split(X_WWW_FORM_URLENCODED_KEY_VALUE_SPLITTER_REGEX, 2);
            bodyMap.put(splitTuple[0], splitTuple[1]);
        }
        for (String field : sensitiveBodyFields) {
            bodyMap.replace(field, PLACEHOLDER);
        }
        return bodyMap.toString();
    }
}
