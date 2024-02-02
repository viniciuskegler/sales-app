package io.github.viniciuskegler;

import io.github.viniciuskegler.domain.entity.Client;
import io.github.viniciuskegler.domain.repository.ClientRepository;
import io.github.viniciuskegler.domain.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@SpringBootApplication
@ComponentScan
@RestController
public class VendasApplication {

    @Value("${application.name}")
    private String applicationName;


    @GetMapping("/hello")
    public String helloWorld() {
        return applicationName;
    }

    @Bean
    public CommandLineRunner init(@Autowired ClientRepository clientRepo,
                                  @Autowired OrderRepository orderRepo){
        return args -> {
            Client c = new Client();
            c.setName("A");

            Client c1 = new Client();
            c.setName("A");

            Client c2 = new Client();
            c.setName("A");

            Client c3 = new Client();
            c.setName("A");

            List<Client> clients = List.of(c, c1, c2, c3);
            clientRepo.saveAll(clients);
        };
    }

    public static void main(String[] args) {
        SpringApplication.run(VendasApplication.class, args);
    }

}
