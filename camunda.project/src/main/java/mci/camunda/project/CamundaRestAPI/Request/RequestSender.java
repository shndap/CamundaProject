package mci.camunda.project.CamundaRestAPI.Request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.*;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.stream.Collectors;

public class RequestSender {
    public static String POST(String targetURL, MultipartEntityBuilder body) throws IOException {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(targetURL);

        HttpEntity httpEntity = body.build();
        httpPost.setEntity(httpEntity);

        CloseableHttpResponse response = httpClient.execute(httpPost);

        return logResponse(response);
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

    public static String GET(String targetURL) throws IOException {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet httpPost = new HttpGet(targetURL);

        CloseableHttpResponse response = httpClient.execute(httpPost);

        return logResponse(response);
    }
}
