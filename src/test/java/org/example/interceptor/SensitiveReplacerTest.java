package org.example.interceptor;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SensitiveReplacerTest {


    @Test
    void replaceSensitiveFromHeader() {
        List<String> headerSensitives = Arrays
                .asList("Connection", "Server", "Content-Length", "Access-Control-Allow-Credentials");
        HttpHeaders headers = new HttpHeaders();
        headers.put("Date", Collections.singletonList("Thu, 08 Dec 2022 10:45:09 GMT"));
        headers.put("Content-Type", Collections.singletonList("application/json"));
        headers.put("Content-Length", Collections.singletonList("547"));
        headers.put("Connection", Collections.singletonList("keep-alive"));
        headers.put("Server", Collections.singletonList("gunicorn/19.9.0"));
        headers.put("Access-Control-Allow-Origin", Collections.singletonList("*"));
        headers.put("Access-Control-Allow-Credentials", Collections.singletonList("true"));
        String expected = "[Date:\"Thu, 08 Dec 2022 10:45:09 GMT\", Content-Type:\"application/json\", Content-Length:\"*****\", Connection:\"*****\", Server:\"*****\", Access-Control-Allow-Origin:\"*\", Access-Control-Allow-Credentials:\"*****\"]";
        SensitiveReplacer replacer = new ApplicationJsonSensitiveReplacer(headerSensitives, Collections.singletonList(""));
        assertEquals(expected, replacer.replaceFromHeaders(headers).toString());
        assertNotEquals(headers.toString(), expected);
        replacer = new XWwwFormUrlencodedSensitiveReplacer(headerSensitives, Collections.emptyList());
        assertEquals(expected, replacer.replaceFromHeaders(headers).toString());
        assertNotEquals(headers.toString(), expected);
    }
}