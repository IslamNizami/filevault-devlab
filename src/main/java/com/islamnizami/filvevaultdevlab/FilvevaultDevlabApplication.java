package com.islamnizami.filvevaultdevlab;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class FilvevaultDevlabApplication {

    public static void main(String[] args) {
        SpringApplication.run(FilvevaultDevlabApplication.class, args);
    }

}
