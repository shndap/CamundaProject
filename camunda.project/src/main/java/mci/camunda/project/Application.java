package mci.camunda.project;

import mci.camunda.project.CamundaRestAPI.CamundaRestAPIController;
import mci.camunda.project.Console.TestCamunda;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.io.IOException;
import java.net.URISyntaxException;

@SpringBootApplication
@EnableJpaRepositories("mci.camunda.project.*")
@ComponentScan(basePackages = {"mci.camunda.project.*"})
@EntityScan("mci.camunda.project.*")
public class Application {
    public static void main(String[] args) throws IOException {
        SpringApplication.run(Application.class, args);

        TestCamunda.run();
    }

}
