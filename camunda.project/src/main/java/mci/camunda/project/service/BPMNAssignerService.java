package mci.camunda.project.service;

import mci.camunda.project.Assigner.BPMN.BPMN;

import java.util.ArrayList;

public interface BPMNAssignerService {

    public BPMN create(BPMN BPMN);
    public BPMN createBPMN(String path);

    public ArrayList<BPMN> getAllBPMNs();

    public BPMN getBPMNByID(int id);

    public BPMN update(BPMN BPMN, int id);

    void deleteBPMN(int id);

    public String assign(int bpmnId, int BPMNId, String componentId);
}
