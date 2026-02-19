package com.example.modulith.vet;

import com.example.modulith.adoptions.DogAdoptedEvent;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
class Dogtor {

    /*@EventListener
    @Async*/
//    private final MyValidation validation;
//
//    Dogtor(MyValidation validation) {
//        this.validation = validation;
//    }


    @ApplicationModuleListener
    void checkup(DogAdoptedEvent dogId)
            throws Exception {
        IO.println("checking up dog " + dogId);
        Thread.sleep(5000);
    }
}
