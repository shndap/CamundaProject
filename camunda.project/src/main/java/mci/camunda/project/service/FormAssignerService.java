package mci.camunda.project.service;

import mci.camunda.project.Assigner.Form.Form;

import java.util.ArrayList;

public interface FormAssignerService {

    public Form create(Form form);
    public Form createForm(String path);

    public ArrayList<Form> getAllForms();

    public Form getFormByID(int id);

    public Form update(Form form, int id);

    void deleteForm(int id);

    public String assign(int bpmnId, int formId, String componentId);
}
