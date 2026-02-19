package com.example.integration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.core.GenericHandler;
import org.springframework.integration.dsl.*;
import org.springframework.integration.file.dsl.Files;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHeaders;

import java.io.File;
import java.time.Duration;

@SpringBootApplication
public class IntegrationApplication {

    public static void main(String[] args) {
        SpringApplication.run(IntegrationApplication.class, args);
    }

    @Bean
    MessageChannelSpec<DirectChannelSpec, DirectChannel> fileChannel() {
        return MessageChannels.direct();
    }

    @Bean
    IntegrationFlow integrationFlow(
            MessageChannel fileChannel,
            @Value("file://${HOME}/Desktop/in") File in,
            @Value("file://${HOME}/Desktop/out") File out
    ) {
        var files = Files.inboundAdapter(in)
                .autoCreateDirectory(true);
        return IntegrationFlow
                .from(files, p -> p.poller(Pollers
                        .fixedRate(Duration.ofMillis(500))))
                .split()
                .enrich()
                .transform()
                .filter()
                .handle((GenericHandler<File>) (payload, headers) -> {
                    IO.println("got a file " + payload + " " + headers);
                    return payload;
                })
                .handle(fileChannel)
                .get();
    }

    @Bean
    IntegrationFlow fileIntegrationPart12(MessageChannel fileChannel) {
        return IntegrationFlow
                .from(fileChannel)
                .handle(new GenericHandler<Object>() {
                    @Override
                    public Object handle(Object payload, MessageHeaders headers) {
                        IO.println("got a file " + payload + " " + headers);
                        return null;
                    }
                })
                .get();
    }

}
