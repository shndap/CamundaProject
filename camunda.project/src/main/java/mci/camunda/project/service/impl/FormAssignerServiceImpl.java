package mci.camunda.project.service.impl;

import mci.camunda.project.Assigner.Assigner;
import mci.camunda.project.Assigner.BPMN.BPMN;
import mci.camunda.project.Assigner.Form.Form;
import mci.camunda.project.repository.BPMNRepository;
import mci.camunda.project.repository.FormRepository;
import mci.camunda.project.service.BPMNAssignerService;
import mci.camunda.project.service.FormAssignerService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;


@Service
public class FormAssignerServiceImpl implements FormAssignerService, BPMNAssignerService {
    private final FormRepository formRepository;
    private final BPMNRepository bpmnRepository;

    public FormAssignerServiceImpl(FormRepository formRepository, BPMNRepository bpmnRepository) {
        this.formRepository = formRepository;
        this.bpmnRepository = bpmnRepository;
    }

    @Override
    public Form create(Form form) {
        return formRepository.save(form);
    }

    @Override
    public Form createForm(String path) {
        Form form = new Form(path);
        return formRepository.save(form);
    }

    private boolean contains(Form form) {
        return formRepository.existsById(form.getId());
    }

    private String assign(BPMN bpmn, Form form, String componentID) {
        if (!contains(form)) create(form);
        Assigner assigner = new Assigner(bpmn);
        if (!assigner.isOkay()) return "Failed";
        assigner.assignFormToID(componentID, form);
        update(new BPMN(assigner.getDocument()), bpmn.getId());
        return "Done";
    }

    @Override
    public ArrayList<Form> getAllForms() {
        return new ArrayList<>(formRepository.findAll());
    }

    @Override
    public Form getFormByID(int id) {
        if (isFormAbsent(id)) return null;
        return formRepository.findById(id).get();
    }

    @Override
    public Form update(Form form, int id) {
        if (isFormAbsent(id)) return null;
        Form form1 = formRepository.findById(id).get();
        form1.setId(form.getId());
        form1.setJson(form.getJson());

        return formRepository.save(form1);
    }

    @Override
    public void deleteForm(int id) {
        formRepository.deleteById(id);
    }

    @Override
    public BPMN create(BPMN bpmn) {
        return bpmnRepository.save(bpmn);
    }

    @Override
    public BPMN createBPMN(String path) {
        BPMN bpmn = new BPMN(path);
        if(!bpmn.isOkay()) return null;
        else return bpmnRepository.save(bpmn);
    }

    @Override
    public ArrayList<BPMN> getAllBPMNs() {
        return new ArrayList<BPMN>(bpmnRepository.findAll());
    }

    @Override
    public BPMN getBPMNByID(int id) {
        if (isBpmnAbsent(id)) return null;
        return bpmnRepository.findById(id).get();
    }

    private boolean isBpmnAbsent(int id) {
        return !bpmnRepository.findById(id).isPresent();
    }

    @Override
    public BPMN update(BPMN bpmn, int id) {
        if (isBpmnAbsent(id)) return null;
        BPMN bpmn1 = bpmnRepository.findById(id).get();
        bpmn1.setId(bpmn.getId());
        bpmn1.setXml(bpmn.getXml());

        return bpmnRepository.save(bpmn1);
    }

    @Override
    public void deleteBPMN(int id) {
        bpmnRepository.deleteById(id);
    }

    @Override
    public String assign(int bpmnId, int formId, String componentId) {
        if (isFormAbsent(formId) || isBpmnAbsent(bpmnId)) return "Failed";
        return assign(bpmnRepository.findById(bpmnId).get(), formRepository.findById(formId).get(), componentId);
    }

    private boolean isFormAbsent(int id) {
        return !formRepository.findById(id).isPresent();
    }
}
