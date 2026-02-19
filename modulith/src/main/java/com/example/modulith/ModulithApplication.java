package com.example.modulith;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.modulith.events.IncompleteEventPublications;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@SpringBootApplication
//@EnableScheduling
public class ModulithApplication {
//
//    public ModulithApplication(IncompleteEventPublications eventPublications) {
//        this.eventPublications = eventPublications;
//    }
//
    public static void main(String[] args) {
        SpringApplication.run(ModulithApplication.class, args);
    }

//
//    private final IncompleteEventPublications eventPublications;
//
//    @Scheduled(cron = "0 0/5 * * * ?")
//    void youIncompleteMeRunner() {
//        this.eventPublications
//                .resubmitIncompletePublications(e -> true);
//    }

}
