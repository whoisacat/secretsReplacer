package org.example;

import org.example.interceptor.ApplicationJsonSensitiveReplacer;
import org.example.interceptor.RequestResponseLoggingInterceptor;
import org.example.interceptor.XWwwFormUrlencodedSensitiveReplacer;
import org.example.model.LoginForm;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;

public class Main {

    private final static String body = "{  \"args\": {},   \"data\": \"{\\\"password\\\":\\\"password\\\",\\\"username\\\":\\\"username\\\"}\",   \"files\": {},   \"form\": {},   \"headers\": {    \"Accept\": \"text/plain, application/json, application/*+json, */*\",     \"Content-Length\": \"45\",     \"Content-Type\": \"application/json\",     \"Host\": \"httpbin.org\",     \"User-Agent\": \"Java/17.0.2\",     \"X-Amzn-Trace-Id\": \"Root=1-6389d11c-17adce885257561b55579a75\"  },   \"json\": {    \"password\": \"password\",     \"username\": \"username\"  },   \"origin\": \"5.189.26.9\",   \"url\": \"http://httpbin.org/post\"}";
    private final static String littleBody = "{\"fileFetchUrl\":null,\"status\":\"success\"}";

    public static void main(String[] args) {

        LoginForm loginForm = new LoginForm("username", "password");
        HttpEntity<LoginForm> requestEntity = new HttpEntity<>(loginForm);
        restTemplate().postForEntity("http://httpbin.org/post", requestEntity, String.class);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, String> body= new LinkedMultiValueMap<>();
        body.add("email", "first.last@example.com");
        body.add("text", "go fuck yourself");
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);
        restTemplate().postForEntity( "http://example.org", request , String.class );
    }

    private static RestTemplate restTemplate() {
        ClientHttpRequestFactory factory = new BufferingClientHttpRequestFactory(new SimpleClientHttpRequestFactory());
        RestTemplate restTemplate = new RestTemplate(factory);
        var interceptors = restTemplate.getInterceptors();
        if (interceptors.isEmpty()) {
            interceptors = new ArrayList<>();
        }
        interceptors.add(new RequestResponseLoggingInterceptor(Arrays
                .asList(new ApplicationJsonSensitiveReplacer(), new XWwwFormUrlencodedSensitiveReplacer())));
        restTemplate.setInterceptors(interceptors);
        return restTemplate;
    }
}
