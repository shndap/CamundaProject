package mci.camunda.project.CamundaRestAPI.Request;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.*;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.springframework.http.ResponseEntity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

public class RequestSender {
    public static ResponseEntity<HttpEntity> POST(String targetURL, MultipartEntityBuilder body) throws IOException {
        return POST(targetURL, body, false);
    }

    public static ResponseEntity<HttpEntity> POST(String targetURL, MultipartEntityBuilder body, boolean json)
            throws IOException {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(targetURL);

        HttpEntity httpEntity = body.build();
        httpPost.setEntity(httpEntity);
        if (json)
            httpPost.setHeader(HTTP.CONTENT_TYPE, "application/json");

        CloseableHttpResponse response = httpClient.execute(httpPost);

        ResponseEntity<HttpEntity> responseEntity = ResponseEntity
                .status(response.getStatusLine().getStatusCode())
                .body(response.getEntity());

        return responseEntity;
    }

    public static ResponseEntity<HttpEntity> POST(String targetURL, String body, boolean json) throws IOException {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(targetURL);

        httpPost.setEntity(new StringEntity(body));
        if (json)
            httpPost.setHeader(HTTP.CONTENT_TYPE, "application/json");

        CloseableHttpResponse response = httpClient.execute(httpPost);

        ResponseEntity<HttpEntity> responseEntity = ResponseEntity
                .status(response.getStatusLine().getStatusCode())
                .body(response.getEntity());

        return responseEntity;
    }

    private static String logResponse(CloseableHttpResponse response) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
        return reader.lines().map(line -> line + "\n").collect(Collectors.joining());
    }

    public static String PUT(String targetURL, MultipartEntityBuilder body) throws IOException {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPut httpPut = new HttpPut(targetURL);

        HttpEntity httpEntity = body.build();
        httpPut.setEntity(httpEntity);

        CloseableHttpResponse response = httpClient.execute(httpPut);

        return logResponse(response);
    }

    public static String DELETE(String targetURL) throws IOException {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpDelete httpPost = new HttpDelete(targetURL);

        CloseableHttpResponse response = httpClient.execute(httpPost);

        return logResponse(response);
    }

    public static ResponseEntity<String> GET(String targetURL) throws IOException {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet httpPost = new HttpGet(targetURL);

        CloseableHttpResponse response = httpClient.execute(httpPost);

        return ResponseEntity.ok(logResponse(response));
    }
}
