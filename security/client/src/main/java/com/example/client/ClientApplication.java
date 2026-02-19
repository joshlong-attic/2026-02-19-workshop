package com.example.client;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.server.mvc.filter.BeforeFilterFunctions;
import org.springframework.cloud.gateway.server.mvc.filter.TokenRelayFilterFunctions;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;

import static org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions.route;
import static org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions.http;

//@ImportHttpServices(ResourceUserClient.class)
@SpringBootApplication
public class ClientApplication {

    public static void main(String[] args) {
        SpringApplication.run(ClientApplication.class, args);
    }

 /*   @Bean
    OAuth2RestClientHttpServiceGroupConfigurer securityConfigurer(
            OAuth2AuthorizedClientManager manager) {
        return OAuth2RestClientHttpServiceGroupConfigurer.from(manager);
    }
*/

    @Bean
    @Order(Ordered.LOWEST_PRECEDENCE)
    RouterFunction<ServerResponse> cdn() {
        return route()
                .GET("/**", http())
                .before(BeforeFilterFunctions.uri("http://localhost:8020"))
                .build();
    }

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    RouterFunction<ServerResponse> api() {
        return route()
                .GET("/api/**", http()) // localhost:8081/api/message
                .before(BeforeFilterFunctions.uri("http://localhost:8082"))
                .filter(TokenRelayFilterFunctions.tokenRelay())
                .before(BeforeFilterFunctions.rewritePath("/api", "/"))
                .build();
    }

}
/*

@Controller
@ResponseBody
class MeController {

    private final ResourceUserClient client ;

    MeController(ResourceUserClient client) {
        this.client = client;
    }

    //    private final RestClient http;
*/
/*

    MeController(OAuth2AuthorizedClientManager manager, RestClient.Builder http) {
        this.http = http
             //   .requestInterceptor(new OAuth2ClientHttpRequestInterceptor(manager))
                .build();
    }

*//*

    @GetMapping("/")
    ResourceUser home(Principal principal,
                      @RegisteredOAuth2AuthorizedClient("spring") OAuth2AuthorizedClient authorizedClient) {
        return this.client.request();

       */
/* return http.get()
                .uri("http://localhost:8082")
                *//*
 */
/*   .headers(headers -> {
                       var tokenValue = authorizedClient.getAccessToken().getTokenValue();
                       IO.println("Bearer token: " + tokenValue);
                       headers.setBearerAuth(tokenValue);
                   })*//*
 */
/*
                .retrieve()
                .body(ResourceUser.class);*//*

    }
}

record ResourceUser(String user) {
}

@ClientRegistrationId("spring")
interface ResourceUserClient {

    @GetExchange("http://localhost:8082")
    ResourceUser request();
}*/
