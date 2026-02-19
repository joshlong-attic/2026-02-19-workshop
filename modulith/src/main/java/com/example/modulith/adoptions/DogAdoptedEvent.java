package com.example.modulith.adoptions;

import org.springframework.modulith.events.Externalized;

@Externalized // ("nameOfAMessageChannelBean")
public record DogAdoptedEvent(int dogId) {
}
