package mci.camunda.project;

import mci.camunda.project.Console.TestCamunda;
import org.camunda.community.rest.client.invoker.ApiException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.io.IOException;

@SpringBootApplication
@EnableJpaRepositories("mci.camunda.project.*")
@ComponentScan(basePackages = {"mci.camunda.project.*"})
@EntityScan("mci.camunda.project.*")
public class Application {
    public static void main(String[] args) throws IOException, ApiException {
        SpringApplication.run(Application.class, args);

        TestCamunda.run();
    }

}
