package mci.camunda.project.Console;

import mci.camunda.project.CamundaRestAPI.CamundaRestAPIController;
import org.camunda.community.rest.client.api.ProcessDefinitionApi;
import org.camunda.community.rest.client.dto.VariableValueDto;
import org.camunda.community.rest.client.invoker.ApiException;
import org.camunda.community.rest.client.springboot.CamundaHistoryApi;

import java.io.IOException;
import java.util.HashMap;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TestCamunda {
    public static CamundaRestAPIController controller;

    public static void run() throws IOException, ApiException {
        controller = new CamundaRestAPIController(
                new ProcessDefinitionApi(),
                new CamundaHistoryApi()
        );

//        System.out.println(controller.createUser("me",
//                "sahand",
//                "akramipour",
//                "s@g.com",
//                "sahand"));

        Scanner scanner = new Scanner(System.in);
        System.out.println("---------------------------------------------");
        while (true) {
            String line = scanner.nextLine();

            if (line.equals("deploy")) {
                deploy();
            } else if (line.equals("all")) {
                getAll();
            } else if (line.contains("start")) {
                Pattern pattern = Pattern.compile("start (?<key>\\w+) (?<businessKey>\\w+)");
                Matcher matcher = pattern.matcher(line);
                matcher.find();
                String key = matcher.group("key");
                String businessKey = matcher.group("businessKey");
                start(key, businessKey);
            } else if (line.contains("claim")) {
                Pattern pattern = Pattern.compile("claim (?<key>\\w+) (?<user>\\w+)");
                Matcher matcher = pattern.matcher(line);
                matcher.find();
                String key = matcher.group("key");
                String user = matcher.group("user");
                claim(user, key);
            } else if (line.contains("complete")) {
                Pattern pattern = Pattern.compile("complete (?<key>\\w+) (?<user>\\w+)");
                Matcher matcher = pattern.matcher(line);
                matcher.find();
                String key = matcher.group("key");
                String user = matcher.group("user");
//                complete(user, key);
            } else {
                System.out.println("Invalid");
            }
        }
    }

//    private static void complete(String user, String key) {
//        System.out.println(controller.completeTask());
//    }

    private static void claim(String user, String key) throws IOException {
        System.out.println(controller.claimTask(user, key));
    }

    private static void start(String key, String businessKey) throws ApiException {
        HashMap<String, VariableValueDto> map = new HashMap<>();

        map.put("variable-one", new VariableValueDto().value(123).type("Integer"));
        map.put("variable-two", new VariableValueDto().value("haha").type("string"));

        System.out.println(controller.startProcess(
                key,
                businessKey,
                map
        ));
    }

    private static void getAll() throws IOException {
        System.out.println(controller.getAllDeployments());
    }

    private static void deploy() throws IOException {
        System.out.println(controller.createDeploy("src/main/resources/Processes/process1.bpmn"));
    }
}
