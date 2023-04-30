package com.secondhand;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Main extends ServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }
}
