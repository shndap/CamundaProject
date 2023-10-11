package mci.camunda.project.CamundaRestAPI;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import mci.camunda.project.CamundaRestAPI.Request.RequestSender;
import org.apache.http.HttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.util.EntityUtils;
import org.camunda.community.rest.client.api.ProcessDefinitionApi;
import org.camunda.community.rest.client.dto.*;
import org.camunda.community.rest.client.invoker.ApiException;
import org.camunda.community.rest.client.springboot.CamundaHistoryApi;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Controller
@RequestMapping("taskApi")
public class CamundaRestAPIController {
    private final ProcessDefinitionApi processDefinitionApi;
    private final CamundaHistoryApi camundaHistoryApi;
    private final TaskController taskController;
    public static final String SERVER = "http://localhost:8080/";
    public static final String ENGINE = SERVER + "engine-rest/";

    public CamundaRestAPIController(ProcessDefinitionApi processDefinitionApi, CamundaHistoryApi camundaHistoryApi) {
        this.processDefinitionApi = processDefinitionApi;
        this.camundaHistoryApi = camundaHistoryApi;
        taskController = new TaskController();
    }

    public CamundaRestAPIController() {
        this.processDefinitionApi = new ProcessDefinitionApi();
        this.camundaHistoryApi = new CamundaHistoryApi();
        taskController = new TaskController();
    }

    public ResponseEntity<String> authorize(String id) throws IOException {
        String url = ENGINE + "id=" + id;
        return RequestSender.GET(url);
    }

    public ResponseEntity<HttpEntity> createDeploy(String path) throws IOException {
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

    public ResponseEntity<String> getAllDeployments() throws IOException {
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

    public ResponseEntity<String> startProcess(String key, String businessKey,
                                               HashMap<String, VariableValueDto> variables) throws ApiException {
        org.camunda.community.rest.client.dto.StartProcessInstanceDto startProcessInstanceDto =
                new StartProcessInstanceDto().variables(variables);

        startProcessInstanceDto.setBusinessKey(businessKey);

        ProcessInstanceWithVariablesDto processInstance = processDefinitionApi.startProcessInstanceByKey(
                key,
                startProcessInstanceDto);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body("Started process instance with id: " + processInstance.getId());
    }

    public ResponseEntity<List<HistoricProcessInstanceDto>> getActiveProcessInstances() throws ApiException {
        List<HistoricProcessInstanceDto> processInstances =
                camundaHistoryApi.historicProcessInstanceApi().queryHistoricProcessInstances(
                        null,
                        null,
                        new HistoricProcessInstanceQueryDto().active(true)
                );
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(processInstances);
    }

    @GetMapping("/create-user")
    public ModelAndView createUserApi() {
        return new ModelAndView("taskApi/create-user");
    }

    @PostMapping("/create-user")
    public ModelAndView createUserApi(
            @RequestParam String id,
            @RequestParam String firstName,
            @RequestParam String lastName,
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam String passwordRepeat
    ) throws IOException {
        ModelAndView modelAndView = new ModelAndView();

        System.out.println("hi");

        if (idExists(id)) {
            modelAndView.getModel().put("errorUserName", "true");
            return modelAndView;
        }

        if (!Objects.equals(password, passwordRepeat)) {
            modelAndView.getModel().put("errorPassword", "true");
            return modelAndView;
        }

        ResponseEntity<HttpEntity> user = createUser(id, firstName, lastName, email, password);

        if (user.getStatusCode().isError()) {
            modelAndView.getModel().put("error", "true");
            return modelAndView;
        }

        HashMap<String, UserDto> allUsers = getAllUsers();
        System.out.println(allUsers);

        taskController.setUser(allUsers.get(id));
        modelAndView.setViewName("taskApi/user-menu");
        modelAndView.getModel().put("username", taskController.getUser().getProfile().getId());
        ArrayList<String> processes = getAllProcesses();
        modelAndView.addObject("processes", processes);

        return modelAndView;
    }

    @GetMapping("/login")
    public ModelAndView login() {
        return new ModelAndView("taskApi/login");
    }

    @PostMapping("/login")
    public ModelAndView loginPOST(@RequestParam String username, @RequestParam String password) throws IOException {
        ModelAndView modelAndView = new ModelAndView();

        HashMap<String, UserDto> allUsers = getAllUsers();

        if (!allUsers.containsKey(username)) {
            modelAndView.getModel().put("error", "Username not found");
            return modelAndView;
        } else if (!allUsers.get(username).getCredentials().getPassword().equals(password)) {
            modelAndView.getModel().put("error", "Password is wrong");
            return modelAndView;
        } else {
            taskController.setUser(allUsers.get(username));

            modelAndView.getModel().put("username", taskController.getUser().getProfile().getId());
            ArrayList<String> processes = getAllProcesses();
            modelAndView.addObject("processes", processes);
            modelAndView.addObject("controller", this);


            modelAndView.setViewName("taskApi/user-menu");
            return modelAndView;
        }
    }

    @GetMapping("/user-menu")
    public ModelAndView userMenu() throws IOException {
        if (taskController.getUser() == null) {
            return new ModelAndView("redirect:taskApi/login");
        }

        ArrayList<String> processes = getAllProcesses();

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.getModel().put("username", taskController.getUser().getProfile().getId());
        modelAndView.addObject("processes", processes);
        modelAndView.addObject("controller", this);

        return modelAndView;
    }

    private ArrayList<String> getAllProcesses() throws IOException {
        String url = ENGINE + "process-definition";

        ResponseEntity<String> response = RequestSender.GET(url);

        ObjectMapper mapper = new ObjectMapper();
        List<LinkedHashMap<String, String>> processDefinitions = mapper.readValue(response.getBody(), List.class);

        HashSet<String> strings = processDefinitions.stream().map(processDefinition -> processDefinition.get("key"))
                .collect(Collectors.toCollection(HashSet::new));
        return new ArrayList<>(strings.stream().toList());
    }

    private ArrayList<String> getAllInstances(String processID) throws IOException {
        String url = ENGINE + "process-instance?processDefinitionKey=" + processID;
        ArrayList<String> id = getStrings(url, "id");
        return id;
    }

    private ArrayList<String> getAllTasks(String processInstanceID) throws IOException {
        String url = ENGINE + "task?processInstanceId=" + processInstanceID;
        return getStrings(url, "id");
    }

    @NotNull
    private ArrayList<String> getStrings(String url, String key) throws IOException {
        ResponseEntity<String> response = RequestSender.GET(url);

        ObjectMapper mapper = new ObjectMapper();
        List<LinkedHashMap<String, String>> processDefinitions = mapper.readValue(response.getBody(), List.class);

        return processDefinitions.stream().map(processDefinition -> processDefinition.get(key)).
                collect(Collectors.toCollection(ArrayList::new));
    }


    @GetMapping("update-instances")
    public ModelAndView updateInstances(@RequestParam("process") String process) throws IOException {
        ModelAndView modelAndView = new ModelAndView();

        taskController.setProcess(new ProcessInstanceDto().id(process));

        modelAndView.addObject("instances", getAllInstances(process));
        modelAndView.addObject("selectedProcess", process);
        modelAndView.addObject("username", taskController.getUser().getProfile().getId());
        modelAndView.addObject("processes", getAllProcesses());

        modelAndView.setViewName("taskApi/user-menu");

        return modelAndView;
    }

    @GetMapping("update-tasks")
    public ModelAndView updateTasks(@RequestParam("instance") String instance) throws IOException {
        ModelAndView modelAndView = new ModelAndView();

        taskController.getProcess().setCaseInstanceId(instance);

        modelAndView.addObject("tasks", getAllTasks(instance));
        modelAndView.addObject("selectedProcess", taskController.getProcess().getId());
        modelAndView.addObject("username", taskController.getUser().getProfile().getId());
        modelAndView.addObject("selectedInstance", instance);
        modelAndView.addObject("processes", getAllProcesses());
        modelAndView.addObject("instances", getAllInstances(taskController.getProcess().getId()));


        modelAndView.setViewName("taskApi/user-menu");

        return modelAndView;
    }

    @GetMapping("selected-task")
    public ModelAndView selectedTask(@RequestParam("task") String task) throws IOException {
        ModelAndView modelAndView = new ModelAndView();

        taskController.setTask(new TaskDto().id(task));

        modelAndView.addObject("tasks", getAllTasks(taskController.getProcess().getCaseInstanceId()));
        modelAndView.addObject("selectedProcess", taskController.getProcess().getId());
        modelAndView.addObject("username", taskController.getUser().getProfile().getId());
        modelAndView.addObject("selectedInstance", taskController.getProcess().getCaseInstanceId());
        modelAndView.addObject("processes", getAllProcesses());
        modelAndView.addObject("instances", getAllInstances(taskController.getProcess().getId()));
        modelAndView.addObject("selectedTask", task);

        modelAndView.setViewName("taskApi/what-to-do-with-task");
        return modelAndView;
    }

    @GetMapping("/logout")
    public ModelAndView logout() {
        taskController.setUser(null);
        return login();
    }

    @PostMapping("/claim-task")
    public ModelAndView claimTaskApi() throws IOException {
        ModelAndView modelAndView = new ModelAndView("taskApi/user-menu");
        ResponseEntity<HttpEntity> stringResponseEntity = claimTask(taskController.getTask().getId(),
                taskController.getUser().getProfile().getId());

        ArrayList<String> processes = getAllProcesses();

        modelAndView.getModel().put("username", taskController.getUser().getProfile().getId());
        modelAndView.addObject("processes", processes);
        modelAndView.addObject("controller", this);

        String response = "";
        if (stringResponseEntity.getBody() != null)
            response = EntityUtils.toString(stringResponseEntity.getBody());

        modelAndView.getModel().put("error",
                stringResponseEntity.getStatusCode().isError() ? "Error: " + response : "Task claimed"
        );

        return modelAndView;
    }

    @PostMapping("/complete-task")
    public ModelAndView completeTaskApi() {
        ModelAndView modelAndView = new ModelAndView("taskApi/complete-task-form");
        modelAndView.addObject("json", "");
        return modelAndView;
    }

    private HashMap<String, UserDto> getAllUsers() throws IOException {
        String url = ENGINE + "user";

        String all = RequestSender.GET(url).toString();

        Matcher matcher = Pattern.compile("\\{[\\s\\S]+?}").matcher(all);

        Gson gson = new Gson();

        HashMap<String, UserDto> out = new HashMap<>();
        while (matcher.find()) {
            UserDto userDto = getUserDto(gson, matcher.group());
            out.put(userDto.getProfile().getId(), userDto);
        }

        return out;
    }

    @NotNull
    private static UserDto getUserDto(Gson gson, String userJson) {
        HashMap<String, String> map = gson.fromJson(userJson, HashMap.class);

        UserDto userDto = new UserDto();
        userDto.setProfile(new UserProfileDto()
                .id(map.getOrDefault("id", ""))
                .firstName(map.getOrDefault("firstName", ""))
                .lastName(map.getOrDefault("lastName", ""))
                .email(map.getOrDefault("email", ""))
        );

        userDto.setCredentials(new UserCredentialsDto()
                .password(map.getOrDefault("password", ""))
        );
        return userDto;
    }

    private boolean idExists(String id) throws IOException {
        return getAllUsers().keySet().contains(id);
    }

    public ResponseEntity<HttpEntity> createUser(String id, String firstName, String lastName, String email, String password) throws IOException {
        System.out.println(id + " " + firstName + " " + lastName + " " + email + " " + password);

        UserProfileDto userProfileDto = new UserProfileDto()
                .id(id)
                .firstName(firstName)
                .lastName(lastName)
                .email(email);

        UserCredentialsDto userCredentialsDto = new UserCredentialsDto()
                .password(password);

        UserDto userDto = new UserDto().profile(userProfileDto).credentials(userCredentialsDto);

        Gson gson = new Gson();
        String s = gson.toJson(userDto);

        String url = ENGINE + "user/create";

        return RequestSender.POST(url, s, true);
    }

    public ResponseEntity<HttpEntity> claimTask(String taskId, String userId) throws IOException {
        String url = ENGINE + "task/" + taskId + "/claim";

        String body = "{\"userId\": \"" + userId + "\"}";

        return RequestSender.POST(url, body, true);
    }

    public ResponseEntity<HttpEntity> completeTask(String taskId,
                                                   HashMap<String, VariableValueDto> variables) throws IOException {
        String multipartVariables = getVariablesJson(variables);
        String url = ENGINE + "task/" + taskId + "/complete";
        return RequestSender.POST(url, multipartVariables, true);
    }

    private String getVariablesJson(HashMap<String, VariableValueDto> variables) {
        CompleteTaskDto completeTaskDto =
                new CompleteTaskDto().variables(variables);
        
        completeTaskDto.setWithVariablesInReturn(true);

        Gson gson = new Gson();

        HashMap<String, Object> json = new HashMap<>();
        json.put("variables", completeTaskDto.getVariables());
        json.put("withVariablesInReturn", completeTaskDto.getWithVariablesInReturn());

        String out = gson.toJson(json);

        System.out.println(out);

        return out;
    }

    @PostMapping("submit-form-complete")
    public ModelAndView submitFormComplete(@RequestParam String jsonText) {
        taskController.setJson(jsonText);

        return finishUpCompleteTask();
    }

    private ModelAndView finishUpCompleteTask() {
        ModelAndView modelAndView = new ModelAndView();

        String responseBody = "";

        try {
            ResponseEntity<HttpEntity> response =
                    completeTask(taskController.getTask().getId(), getVariables(taskController.getJson()));

            if (response.getStatusCode().isError()) {
                if (response.getBody() != null)
                    responseBody = EntityUtils.toString(response.getBody());
                modelAndView.addObject("result", "Error: " + responseBody);
            } else {
                modelAndView.addObject("result", "Task completed");
            }
        } catch (IOException e) {
            modelAndView.addObject("result", "An error occurred");
        }

        System.out.println(modelAndView.getModel().get("result"));

        modelAndView.setViewName("taskApi/done");
        return modelAndView;
    }

    @NotNull
    private HashMap<String, VariableValueDto> getVariables(String jsonText) {
        Gson gson = new Gson();
        ArrayList<LinkedTreeMap<String, String>> map = gson.fromJson(jsonText, ArrayList.class);
        HashMap<String, VariableValueDto> variables = new HashMap<>();
        for (LinkedTreeMap<String, String> tree : map) {
            addVariable(variables, tree);
        }
        return variables;
    }

    private void addVariable(HashMap<String, VariableValueDto> variables, LinkedTreeMap<String, String> tree) {
        String name = tree.getOrDefault("name", "");
        String value = tree.getOrDefault("value", "0");
        String type = tree.getOrDefault("type", "String");

        VariableValueDto valueDto = new VariableValueDto().type(type).value(value);
        variables.put(name, valueDto);
    }

    private String toIndentedString(Object o) {
        return o == null ? "null" : o.toString().replace("\n", "\n    ");
    }
}
