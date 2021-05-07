package net.vanderkast.fs4r.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Fs4rApplication {
    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(Fs4rApplication.class);
        application.run(args);
    }
}