package com.example.direnv;

import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

@SpringBootApplication
public class DirenvApplication {

    public static void main(String[] args) {
        SpringApplication.run(DirenvApplication.class, args);

        IO.println("the password is " + System.getenv("SUPER_IMPORTANT_PASSWORD"));
    }

    @Bean
    ApplicationRunner runner(Environment environment) {
        return args -> IO.println(environment.getProperty("spring.datasource.password"));
    }

}
