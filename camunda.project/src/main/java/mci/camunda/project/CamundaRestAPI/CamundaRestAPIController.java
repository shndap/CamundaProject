package mci.camunda.project.CamundaRestAPI;

import com.google.gson.JsonObject;
import mci.camunda.project.CamundaRestAPI.Request.RequestSender;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.camunda.community.rest.client.api.ProcessDefinitionApi;
import org.camunda.community.rest.client.dto.*;
import org.camunda.community.rest.client.invoker.ApiException;
import org.camunda.community.rest.client.springboot.CamundaHistoryApi;
import org.h2.util.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public class CamundaRestAPIController {
    private final ProcessDefinitionApi processDefinitionApi;
    private final CamundaHistoryApi camundaHistoryApi;
    public static final String SERVER = "http://localhost:8080/";
    public static final String ENGINE = SERVER + "engine-rest/";

    public CamundaRestAPIController(ProcessDefinitionApi processDefinitionApi, CamundaHistoryApi camundaHistoryApi) {
        this.processDefinitionApi = processDefinitionApi;
        this.camundaHistoryApi = camundaHistoryApi;
    }

    public void authorize(String id) throws IOException {
        String url = ENGINE + "id=" + id;
        System.out.println(RequestSender.GET(url));
    }

    public String createDeploy(String path) throws IOException {
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

    public String getAllDeployments() throws IOException {
        String url = ENGINE + "process-definition";

        return RequestSender.GET(url);
    }

    public String deleteProcessByKey(String key) throws ApiException {
        processDefinitionApi.deleteProcessDefinitionsByKey(key, false, false, false);
        return "Deleted";
    }

    public String updateTimeToLive(String key, String time) throws IOException {
        String url = ENGINE + "process-definition/key/" + key + " /history-time-to-live";

        StringBody timeToLive = new StringBody(time, ContentType.TEXT_PLAIN);

        MultipartEntityBuilder body = MultipartEntityBuilder.create()
                .addPart("historyTimeToLive", timeToLive);

        return RequestSender.PUT(url, body);
    }

    public String startProcess(String key, String businessKey,
                               HashMap<String, VariableValueDto> variables) throws ApiException {
        org.camunda.community.rest.client.dto.StartProcessInstanceDto startProcessInstanceDto =
                new StartProcessInstanceDto().variables(variables);

        startProcessInstanceDto.setBusinessKey(businessKey);

        try {
            ProcessInstanceWithVariablesDto processInstance = processDefinitionApi.startProcessInstanceByKey(
                    key,
                    startProcessInstanceDto);
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body("Started process instance with id: " + processInstance.getId())
                    .toString();
        }catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getActiveProcessInstances() throws ApiException {
        List<HistoricProcessInstanceDto> processInstances =
                camundaHistoryApi.historicProcessInstanceApi().queryHistoricProcessInstances(
                        null,
                        null,
                        new HistoricProcessInstanceQueryDto().active(true)
                );
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(processInstances)
                .toString();
    }

    public String createUser(String id, String firstName, String lastName, String email, String password) throws IOException {
        StringBody credentials = new StringBody(
                "{\"password\":\"" + password + "\"}"
                , ContentType.APPLICATION_JSON);

        StringBody profile = new StringBody(
                "{\"id\": \"" + id + "\",\n" +
                "  \"firstName\":\"" + firstName + "\",\n" +
                "  \"lastName\":\"" + lastName + "\",\n" +
                "  \"email\":\"" + email + "\"}"
                , ContentType.APPLICATION_JSON);

        String url = ENGINE + "user/create";

        MultipartEntityBuilder body = MultipartEntityBuilder.create();
        body.addPart("profile", profile);
        body.addPart("credentials", credentials);

        return RequestSender.POST(url, body);
    }
}
