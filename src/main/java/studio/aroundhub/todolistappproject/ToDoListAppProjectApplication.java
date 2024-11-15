package studio.aroundhub.todolistappproject;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class ToDoListAppProjectApplication {

    public static void main(String[] args) {
        SpringApplication.run(ToDoListAppProjectApplication.class, args);
    }

}
