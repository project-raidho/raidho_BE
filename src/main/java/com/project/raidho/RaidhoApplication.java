package com.project.raidho;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
@EnableAspectJAutoProxy
@EnableJpaAuditing
public class RaidhoApplication {

    public static void main(String[] args) {
        SpringApplication.run(RaidhoApplication.class, args);
    }

}
