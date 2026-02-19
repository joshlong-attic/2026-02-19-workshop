package com.example.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.security.Principal;
import java.util.Map;

@SpringBootApplication
public class ServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServerApplication.class, args);
    }

}


@Controller
@ResponseBody
class MeController {

    @GetMapping("/")
    Map<String, String> home(Principal principal) {
        return Map.of("user", principal.getName());
    }

    @GetMapping("/message")
    Map<String, String> message(Principal principal) {
        return Map.of("user", "Hello " + principal.getName());
    }
}

