package org.example;

import org.springframework.http.HttpEntity;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;

public class Main {

    private final static String body = "{  \"args\": {},   \"data\": \"{\\\"password\\\":\\\"password\\\",\\\"username\\\":\\\"username\\\"}\",   \"files\": {},   \"form\": {},   \"headers\": {    \"Accept\": \"text/plain, application/json, application/*+json, */*\",     \"Content-Length\": \"45\",     \"Content-Type\": \"application/json\",     \"Host\": \"httpbin.org\",     \"User-Agent\": \"Java/17.0.2\",     \"X-Amzn-Trace-Id\": \"Root=1-6389d11c-17adce885257561b55579a75\"  },   \"json\": {    \"password\": \"password\",     \"username\": \"username\"  },   \"origin\": \"5.189.26.9\",   \"url\": \"http://httpbin.org/post\"}";
    private final static String littleBody = "{\"fileFetchUrl\":null,\"status\":\"success\"}";

    public static void main(String[] args) {

        //todo вынести в тесты и сделать стартер
        LoginForm loginForm = new LoginForm("username", "password");
        HttpEntity<LoginForm> requestEntity = new HttpEntity<>(loginForm);
        restTemplate().postForEntity("http://httpbin.org/post", requestEntity, String.class);
    }

    private static RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        var interceptors = restTemplate.getInterceptors();
        if (interceptors.isEmpty()) {
            interceptors = new ArrayList<>();
        }
        interceptors.add(new RequestResponseLoggingInterceptor(new SensitiveReplacer()));
        restTemplate.setInterceptors(interceptors);
        return restTemplate;
    }
}
