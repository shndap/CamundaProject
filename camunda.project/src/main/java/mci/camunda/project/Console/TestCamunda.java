package mci.camunda.project.Console;

import mci.camunda.project.CamundaRestAPI.CamundaRestAPIController;

import java.io.IOException;
import java.util.Scanner;

public class TestCamunda {
    public static void run() throws IOException {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            String line = scanner.nextLine();

            if(line.equals("deploy")) {
                deploy();
            } else {
                System.out.println("Invalid");
            }
        }
    }

    private static void deploy() throws IOException {
        System.out.println(CamundaRestAPIController.post("src/main/resources/Processes/process.bpmn"));
    }


}
