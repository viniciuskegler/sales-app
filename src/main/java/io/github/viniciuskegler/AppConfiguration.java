package io.github.viniciuskegler;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

public class AppConfiguration {

    @Bean
    public CommandLineRunner execRuntime(){
        return args -> {
            System.out.println("Running development config.");
        };
    }
}
