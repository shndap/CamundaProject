package mci.camunda.project.CamundaRestAPI;

import org.camunda.community.rest.client.dto.ProcessInstanceDto;
import org.camunda.community.rest.client.dto.TaskDto;
import org.camunda.community.rest.client.dto.UserDto;

public class TaskController {
    private UserDto user;
    private TaskDto task;
    private ProcessInstanceDto process;
    private String json;

    public String getJson() {
        return json;
    }

    public void setJson(String json) {
        this.json = json;
    }

    public TaskController(UserDto user, TaskDto task, ProcessInstanceDto process) {
        this.user = user;
        this.task = task;
        this.process = process;
    }

    public TaskController() {
    }

    public UserDto getUser() {
        return user;
    }

    public void setUser(UserDto user) {
        this.user = user;
    }

    public TaskDto getTask() {
        return task;
    }

    public void setTask(TaskDto task) {
        this.task = task;
    }

    public ProcessInstanceDto getProcess() {
        return process;
    }

    public void setProcess(ProcessInstanceDto process) {
        this.process = process;
    }
}
