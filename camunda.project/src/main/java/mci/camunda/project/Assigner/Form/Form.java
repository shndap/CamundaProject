package mci.camunda.project.Assigner.Form;

import lombok.Data;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

@Data
@Entity
public class Form {
    private String json;

    @Id
    @GeneratedValue
    private Integer id;
    public Form(String path) {
        json = initJson(path);
    }

    public Form() {

    }

    private String initJson(String path) {
        StringBuilder builder = new StringBuilder();

        try (BufferedReader buffer = new BufferedReader(new FileReader(path))) {
            String str;
            while ((str = buffer.readLine()) != null) builder.append(str).append("\n");
        } catch (IOException e) {
            return null;
        }

        return builder.toString();
    }

    public Node getNode(Document doc) {
        Element element = doc.createElement("zeebe:userTaskForm");
        element.setAttribute("id", getXMLID());
        Text text = doc.createTextNode(getJson());
        element.appendChild(text);
        return element;
    }

    public String getXMLID() {
        return "form_" + Integer.toHexString(this.hashCode()).toUpperCase();
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public String getJson() {
        return json;
    }

    public void setJson(String json) {
        this.json = json;
    }
}
