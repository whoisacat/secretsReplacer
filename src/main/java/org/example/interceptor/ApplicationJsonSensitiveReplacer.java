package org.example.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import java.io.IOException;
import java.util.*;
public class ApplicationJsonSensitiveReplacer implements SensitiveReplacer {
    private static String JSON_FIELD_SPLITTER_REGEX = "\\.";
    @Override
    public String replaceSensitive(String body, MediaType mediaType) {
        if (!mediaType.toString().contains(MediaType.APPLICATION_JSON_VALUE)) {
            return body;
        }
        try {
            Map<String, Object> map = new ObjectMapper().readValue(body, HashMap.class);
            for (String field : sensitiveBodyFields) {
                String[] keys = field.split(JSON_FIELD_SPLITTER_REGEX);
                Map<String, Object> entrance = map;
                for (String key : keys) {
                    if (!entrance.containsKey(key)) {
                        break;
                    }
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
}
