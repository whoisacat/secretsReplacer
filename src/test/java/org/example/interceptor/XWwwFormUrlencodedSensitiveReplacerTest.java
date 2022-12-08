package org.example.interceptor;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class XWwwFormUrlencodedSensitiveReplacerTest {

    @Test
    void replaceSensitiveFromRequestBody() {
        String requestBody = "email=first.last%40example.com&text=chto-to+poprilichneye";
        SensitiveReplacer replacer = new XWwwFormUrlencodedSensitiveReplacer(Collections.emptyList(),
                Collections.singletonList("email"));
        String expectedBody = "{text=chto-to+poprilichneye, email=*****}";
        assertEquals(expectedBody, replacer.replaceSensitive(requestBody, MediaType.APPLICATION_FORM_URLENCODED));
        replacer = new XWwwFormUrlencodedSensitiveReplacer(Collections.emptyList(), Collections.singletonList("text"));
        expectedBody = "{text=*****, email=first.last%40example.com}";
        assertEquals(expectedBody, replacer.replaceSensitive(requestBody, MediaType.APPLICATION_FORM_URLENCODED));
    }

    @Test
    void replaceSensitiveFromResponseBody() {
        List<String> bodySensitives = Arrays.asList("headers", "data", "json.password", "json.username");
        String responseBody = "{  \"args\": {},   \"data\": \"{email=first.last%40example.com&text=chto-to+"
                + "poprilichneye}\",   \"files\": {},   \"form\": {},   \"headers\": {    \"Accept\": \"text/plain,"
                + " application/json, application/*+json, */*\",     \"Content-Length\": \"45\",     \"Content-Type\": "
                + "\"application/json\",     \"Host\": \"httpbin.org\",     \"User-Agent\": \"Java/17.0.2\",     "
                + "\"X-Amzn-Trace-Id\": \"Root=1-6391c1f9-502fbfc150b6f9fb1dbe06fe\"  },   \"json\": {    \"password\":"
                + " \"password\",     \"username\": \"username\"  },   \"origin\": \"185.15.62.60\",   \"url\": "
                + "\"http://httpbin.org/post\"}";
        SensitiveReplacer replacer = new ApplicationJsonSensitiveReplacer(Collections.emptyList(), bodySensitives);
        String expected = "{args={}, headers=*****, data=*****, form={}, origin=185.15.62.60, files={}, "
                + "json={password=*****, username=*****}, url=http://httpbin.org/post}";
        assertEquals(expected, replacer.replaceSensitive(responseBody, MediaType.APPLICATION_JSON));
    }

    @Test
    void dontReplaceSensitiveFromResponseBodyWithoutApplicationJsonInHeader() {
        List<String> bodySensitives = Arrays
                .asList("Connection", "Server", "Content-Length", "Access-Control-Allow-Credentials");
        String responseBody = "{  \"args\": {},   \"data\": \"{\\\"password\\\":\\\"password\\\",\\\"username\\\":\\\""
                + "username\\\"}\",   \"files\": {},   \"form\": {},   \"headers\": {    \"Accept\": \"text/plain,"
                + " application/json, application/*+json, */*\",     \"Content-Length\": \"45\",     \"Content-Type\":"
                + " \"application/json\",     \"Host\": \"httpbin.org\",     \"User-Agent\": \"Java/17.0.2\",     "
                + "\"X-Amzn-Trace-Id\": \"Root=1-6391c1f9-502fbfc150b6f9fb1dbe06fe\"  },   \"json\": {    \"password\":"
                + " \"password\",     \"username\": \"username\"  },   \"origin\": \"185.15.62.60\",   \"url\": "
                + "\"http://httpbin.org/post\"}";
        SensitiveReplacer replacer = new XWwwFormUrlencodedSensitiveReplacer(Collections.emptyList(), bodySensitives);
        assertEquals(responseBody, replacer.replaceSensitive(responseBody, MediaType.APPLICATION_JSON));
    }
}
