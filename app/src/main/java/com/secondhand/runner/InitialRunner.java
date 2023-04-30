package com.secondhand.runner;


import lombok.AllArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;

import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class InitialRunner implements ApplicationRunner {

    private final InitialService initialService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        System.out.println("This is an initial runner.");
        initialService.init();
    }
}
