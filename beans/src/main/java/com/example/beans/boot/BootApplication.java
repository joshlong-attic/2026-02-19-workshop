package com.example.beans.boot;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@EnableConfigurationProperties(AppConfiguration.class)
@SpringBootApplication
public class BootApplication {

  /*  public static void main(String[] args) {
      //  SpringApplication.run(BootApplication.class, args);
    }
*/
    @Component
    static class MyRunner implements ApplicationRunner {

        private final JdbcCustomerService customerService;

        MyRunner(JdbcCustomerService customerService) {
            this.customerService = customerService;
        }

        @Override
        public void run(ApplicationArguments args) throws Exception {
            Customer customerById = this.customerService.getCustomerById(1);
        }
    }
}

@Component
class EnvironmentLister {

    @Bean
    ApplicationRunner runner(Environment environment,
                             AppConfiguration configuration,
                             @Value("${app.message}") String message) {
        return a -> IO.println(
                environment.getProperty("app.message") + ":" +
                        message + ":" +
                        configuration.message() + configuration.truth()
        );
    }

}


@ConfigurationProperties (prefix = "app")
record AppConfiguration(String message , int truth) {
}

@Service
class JdbcCustomerService {

    @Transactional
    Customer getCustomerById(int id) {
        return new Customer(id, "name-" + id);
    }
}


record Customer(int id, String name) {
}