package com.example.beans.aot;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.aot.hint.TypeReference;
import org.springframework.beans.factory.aot.BeanFactoryInitializationAotContribution;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ImportRuntimeHints;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.jdbc.core.dialect.JdbcH2Dialect;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

//@RegisterReflectionForBinding (Cart.class)
@ImportRuntimeHints(AotApplication.Hints.class)
@SpringBootApplication
public class AotApplication {

    public static void main(String[] args) {
        SpringApplication.run(AotApplication.class, args);
    }

    private static final ClassPathResource MESSAGE = new ClassPathResource("/message");

    static class Hints implements RuntimeHintsRegistrar {

        @Override
        public void registerHints(@NonNull RuntimeHints hints,
                                  @Nullable ClassLoader classLoader) {

            // resources
            // jni
            // reflection
            // jdk proxies (* special attention required for cglib/spring proxies)
            // serialization
            hints.resources().registerResource(MESSAGE);

        }
    }

    @Bean
    ApplicationRunner hello() {
        return a -> IO.println(MESSAGE.getContentAsString(Charset.defaultCharset()));
    }

    @Bean
    static BeanFactoryInitializationAotProcessor beanFactoryInitializationAotProcessor() {
        return new BeanFactoryInitializationAotProcessor();
    }

    @Bean
    JdbcH2Dialect jdbcDialect() {
        return JdbcH2Dialect.INSTANCE;
    }
}

class BeanFactoryInitializationAotProcessor
        implements
        org.springframework.beans.factory.aot.BeanFactoryInitializationAotProcessor {

    @Override
    public @Nullable BeanFactoryInitializationAotContribution processAheadOfTime(
            ConfigurableListableBeanFactory beanFactory) {

        Set<TypeReference> classes = new HashSet<>();
        for (String beanName : beanFactory.getBeanDefinitionNames()) {
            BeanDefinition beanDefinition = beanFactory.getBeanDefinition(beanName);
            Class<?> clzz = beanFactory.getType(beanName);
            if (Serializable.class.isAssignableFrom(clzz)) {
                classes.add(TypeReference.of(clzz.getName()));
            }
        }
        return (ctx, code) -> {

            code.getMethods()
                    .add("hi",
                            builder -> builder.addStatement("""
                                    IO.println("hi from generated code!");
                                    """));

            RuntimeHints runtimeHints = ctx.getRuntimeHints();
            for (TypeReference typeReference : classes) {
                runtimeHints.serialization().registerType(typeReference);
                IO.println("registering " + typeReference);
            }
        };
    }
}

@Component
class Cart implements Serializable {
}

record Cat(int id, String name) {
}

interface CatRepository extends ListCrudRepository<Cat, Integer> {

    Collection <Cat> findByName (String name);
}