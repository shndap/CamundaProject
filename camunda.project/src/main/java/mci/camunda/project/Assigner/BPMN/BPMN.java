package mci.camunda.project.Assigner.BPMN;

import mci.camunda.project.Assigner.BPMNLoader.DocumentLoader;
import lombok.Data;
import org.w3c.dom.Document;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

@Data
@Entity
public class BPMN {
    private boolean isOkay;
    private String xml;
    @Id
    @GeneratedValue
    private Integer id;

    public BPMN(String path){
        try {
            xml = DocumentLoader.convertToString(DocumentLoader.getBPMN(path));
            isOkay = true;
        } catch (Exception e) {
            isOkay = false;
        }
    }

    public BPMN(Document document){
        try {
            xml = DocumentLoader.convertToString(document);
            isOkay = true;
        } catch (Exception e) {
            isOkay = false;
        }
    }

    public BPMN() {

    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public boolean isOkay() {
        return isOkay;
    }
}
