package org.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RequestResponseLoggingInterceptorJava implements ClientHttpRequestInterceptor {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException
    {
        logRequest(request, body);
        ClientHttpResponse response = execution.execute(request, body);
        logResponse(response);
        response.getHeaders().add("headerName", "VALUE");

        return response;
    }

    private void logRequest(HttpRequest request, byte[] body) throws IOException
    {
        if (log.isDebugEnabled())
        {
            log.debug("===========================request begin================================================");
            log.debug("URI         : {}", request.getURI());
            log.debug("Method      : {}", request.getMethod());
            log.debug("Headers     : {}", request.getHeaders());
            log.debug("Request body: {}", new String(body, "UTF-8"));
            log.debug("==========================request end================================================");
        }
    }

    private void logResponse(ClientHttpResponse response) throws IOException
    {
        if (log.isDebugEnabled())
        {
            log.debug("============================response begin==========================================");
            log.debug("Status code  : {}", response.getStatusCode());
            log.debug("Status text  : {}", response.getStatusText());
            log.debug("Headers      : {}", clearHttpHeadersFromSensitive(response));
            log.debug("Response body: {}",
                    clearHttpBodyFromSensitive(
                            StreamUtils.copyToString(response.getBody(), Charset.defaultCharset())
                            .replace("\n", "")));
            log.debug("=======================response end=================================================");
        }
    }


    private String clearHttpBodyFromSensitive(String bodyString) {
        for (String field : sensitiveBodyFields) {
            String value = findFieldValueByRegex(bodyString, field);
            if (value != null) {
                String regex = "\"" +field + "\":\s*" + "\"" + value + "\"";
                bodyString = bodyString.replaceAll(regex, "\"" + field + "\":" + "\"*****\"");
            }
            //todo для объектов и массивов (можно использовать replaceAll с regex), если понадобится
        }
        return bodyString;
    }

    private String clearHttpHeadersFromSensitive(ClientHttpResponse response) {
        HttpHeaders headers = response.getHeaders();
        String headersString = headers.toString();
        for(String field : sensitiveHeaderFields) {
            List<String> sensitive = headers.get(field);
            if (sensitive != null) {
                if (sensitive.size() > 0) {
                    for (String s : sensitive) {
                        headersString = headersString.replace(s, "*****");
                    }
                }
            }
        }
        return headersString;
    }

    private String findFieldValueByRegex(String initial, String fieldname) {
        Pattern pattern = Pattern.compile("\"" + fieldname + "\"\s*:\s*\"[^\"]+\"");
        Matcher matcher = pattern.matcher(initial);
        if (matcher.find()) {
            String match = matcher.group();
            System.out.println(match);
            String val = match.split(":", 2)[1].trim();
            System.out.println(val);
            return val.substring(1, val.length() - 1);
        } else {
            return null;
        }
    }

    //todo внедрить через DI
    private final List<String> sensitiveHeaderFields = Arrays.asList("Server", "Connection");
    private final List<String>  sensitiveBodyFields = Arrays.asList("User-Agent", "Host", "password", "username");
    private final List<String>  sensitiveBodyArrays = Arrays.asList("networkMap", "data");
    private final List<String>  sensitiveBodyObjects = Arrays.asList("data", "headers");
}