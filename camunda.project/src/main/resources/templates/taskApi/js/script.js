function selectedProcess() {
    let processOptions = document.getElementById("process");
    let process = processOptions.options[processOptions.selectedIndex].value;
    window.location = 'update-instances?process=' + process;
}

function selectedInstance() {
    let instanceOptions = document.getElementById("instance");
    let instance = instanceOptions.options[instanceOptions.selectedIndex].value;
    window.location = 'update-tasks?instance=' + instance;
}

function selectedTask() {
    console.log("hereee");
    let taskOptions = document.getElementById("task");
    let task = taskOptions.options[taskOptions.selectedIndex].value;
    window.location = 'selected-tasks?task=' + task;
}