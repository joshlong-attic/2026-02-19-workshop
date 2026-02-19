package com.example.modulith.adoptions;

import com.example.modulith.adoptions.validation.MyValidation;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;
import org.springframework.data.annotation.Id;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@ResponseBody
class AdoptionsController {

    private final AdoptionsService adoptionsService;

    AdoptionsController(MyValidation myValidation,
                        AdoptionsService adoptionsService) {
        this.adoptionsService = adoptionsService;
    }

    @PostMapping("/dogs/{dogId}/adoptions")
    void adopt(@PathVariable int dogId, @RequestParam String owner) {
        this.adoptionsService.adopt(dogId, owner);
    }
}

@Component
class Listener {

    @EventListener
    void on(ApplicationReadyEvent applicationEvent) {
        IO.println("got ready event " + applicationEvent);
    }

    @Bean
    ApplicationRunner runner() {
        return args -> IO.println("runner");
    }

  /*  @EventListener
    void on (ApplicationEvent applicationEvent ) {
        IO.println("got event " + applicationEvent);
    }*/
}

@Service
@Transactional
class AdoptionsService {

    private final DogRepository repository;
    private final ApplicationEventPublisher publisher;

    AdoptionsService(ApplicationEventPublisher applicationEventPublisher,
                     DogRepository repository, ApplicationEventPublisher publisher) {
        this.repository = repository;
        this.publisher = publisher;
    }

    void adopt(int dogId, String owner) {
        this.repository.findById(dogId).ifPresent(dog -> {
            var adopted = this.repository.save(
                    new Dog(dog.id(), dog.name(), owner, dog.description())
            );
            IO.println("adopted " + adopted);
            publisher.publishEvent(new DogAdoptedEvent(adopted.id()));
        });
    }
}


interface DogRepository extends ListCrudRepository<Dog, Integer> {
}

record Dog(@Id int id, String name, String owner, String description) {
}
