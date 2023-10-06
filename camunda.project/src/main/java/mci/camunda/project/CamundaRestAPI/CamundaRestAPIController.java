package mci.camunda.project.CamundaRestAPI;

import mci.camunda.project.CamundaRestAPI.Request.RequestSender;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;

import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;


public class CamundaRestAPIController {
    public static final String SERVER = "http://localhost:8080/";
    public static final String ENGINE = SERVER + "engine-rest/";


    public static void authorize(String id) throws URISyntaxException, IOException, InterruptedException {
        String url = ENGINE + "id=" + id;
        System.out.println(RequestSender.GET(url));
    }

    public static String post(String path) throws IOException {
        String url = ENGINE + "deployment/create";

        StringBody deploymentName = new StringBody("myDeployment", ContentType.TEXT_PLAIN);
        StringBody enableDuplicateFiltering = new StringBody("true", ContentType.TEXT_PLAIN);
        StringBody deployChangedOnly = new StringBody("true", ContentType.TEXT_PLAIN);

        MultipartEntityBuilder builder = MultipartEntityBuilder.create()
                .addPart("deployment-name", deploymentName)
                .addPart("enable-duplicate-filtering", enableDuplicateFiltering)
                .addPart("deploy-changed-only", deployChangedOnly);

        File file = new File(path);
        FileBody fileBody = new FileBody(file);
        builder.addPart(file.getName(), fileBody);

        return RequestSender.POST(url, builder);
    }
}
