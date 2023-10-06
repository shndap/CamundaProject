package mci.camunda.project.Controller;

import mci.camunda.project.Assigner.BPMN.BPMN;
import mci.camunda.project.Assigner.Form.Form;
import mci.camunda.project.service.impl.FormAssignerServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@org.springframework.stereotype.Controller
@RequestMapping("/myApi")
public class FormAssignerRestController {
    @Autowired
    private final FormAssignerServiceImpl formAssignerService;

    public FormAssignerRestController(FormAssignerServiceImpl formAssignerService) {
        this.formAssignerService = formAssignerService;
    }

    @PostMapping("/form/assign/{formID}/to/{componentId}/of/{bpmnId}/")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ResponseEntity<String> assignFormById(@PathVariable int bpmnId,
                                                 @PathVariable String componentId,
                                                 @PathVariable int formID) {
        return ResponseEntity.ok(formAssignerService.assign(bpmnId, formID, componentId));
    }

    /**
     * Form
     **/

    @PostMapping("/form/create")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ResponseEntity<Form> createForm(@RequestBody Form form) {
        return ResponseEntity.ok(formAssignerService.create(form));
    }

    @PostMapping("/form/createByPath")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ResponseEntity<Form> createFormByPath(@RequestBody String path) {
        return ResponseEntity.ok(formAssignerService.createForm(path));
    }

    @GetMapping("/form/get/all")
    public ResponseEntity<ArrayList<Form>> getAllForms() {
        return ResponseEntity.ok(formAssignerService.getAllForms());
    }

    @GetMapping("/form/get/{id}")
    public ResponseEntity<Form> getFormById(@PathVariable int id) {
        return ResponseEntity.ok(formAssignerService.getFormByID(id));
    }

    @PutMapping("/form/update/{id}")
    public ResponseEntity<Form> updateStudent(@RequestBody Form form, @PathVariable("id") int id) {
        return ResponseEntity.ok(formAssignerService.update(form, id));
    }

    @DeleteMapping("/form/delete/{id}")
    public ResponseEntity<String> deleteForm(@PathVariable int id) {
        formAssignerService.deleteForm(id);
        return ResponseEntity.ok("Deleted");
    }

    /**
     * BPMN
     **/

    @PostMapping("/bpmn/create")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ResponseEntity<BPMN> createBPMN(@RequestBody BPMN bpmn) {
        return ResponseEntity.ok(formAssignerService.create(bpmn));
    }

    @PostMapping("/bpmn/createByPath")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ResponseEntity<BPMN> createBPMNByPath(@RequestBody String path) {
        return ResponseEntity.ok(formAssignerService.createBPMN(path));
    }

    @GetMapping("/bpmn/get/all")
    public ResponseEntity<ArrayList<BPMN>> getAllBPMNs() {
        return ResponseEntity.ok(formAssignerService.getAllBPMNs());
    }

    @GetMapping("/bpmn/get/{id}")
    public ResponseEntity<BPMN> getBPMNById(@PathVariable int id) {
        return ResponseEntity.ok(formAssignerService.getBPMNByID(id));
    }

    @PutMapping("/bpmn/update/{id}")
    public ResponseEntity<BPMN> updateStudent(@RequestBody BPMN bpmn, @PathVariable("id") int id) {
        return ResponseEntity.ok(formAssignerService.update(bpmn, id));
    }

    @DeleteMapping("/bpmn/delete/{id}")
    public ResponseEntity<String> deleteBPMN(@PathVariable int id) {
        formAssignerService.deleteBPMN(id);
        return ResponseEntity.ok("Deleted");
    }
}
