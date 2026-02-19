package com.example.beans.basics;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanRegistrar;
import org.springframework.beans.factory.BeanRegistry;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.annotation.*;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.UUID;

import static com.example.beans.basics.Bar.NAME;

public class BeansApplication {

 /*   public static void main(String[] args) {

        var config = new AnnotationConfigApplicationContext(Config.class);
        var foo = config.getBean(Foo.class);
        config.close();


    }*/
}

class MyBeanRegistrar implements BeanRegistrar {

    @Override
    public void register(BeanRegistry registry, Environment env) {

        registry.registerBean(Bar.class);
        registry.registerBean(Foo.class, spec ->
                spec.supplier(supplierContext -> new Foo(UUID.randomUUID() + "", supplierContext.bean(Bar.class))));
    }
}

@Configuration
class OtherConfig {

    @Bean
    @Description("this is a initializing bean")
    InitializingBean initializingBean() {
        return () -> IO.println("bar");
    }
}

@Import({OtherConfig.class /*MyBeanRegistrar.class*/})
@ComponentScan
@Configuration
class Config {

    @Bean
    Foo foo(Bar bar) {
        return new Foo(UUID.randomUUID().toString(),
                bar);
    }

//    @Bean
//    Bar bar() {
//        return new Bar();
//    }

    @Bean
    static MyBeanPostProcessor myBeanPostProcessor() {
        return new MyBeanPostProcessor();
    }

    @Bean
    static MyBeanFactoryPostProcessor myBeanFactoryPostProcessor() {
        return new MyBeanFactoryPostProcessor();
    }
}

class MyNewInitializingBean implements InitializingBean {

    @Override
    public void afterPropertiesSet() throws Exception {
        IO.println("yooooo!");
    }
}

class MyBeanFactoryPostProcessor implements
        org.springframework.beans.factory.config.BeanFactoryPostProcessor,
        BeanDefinitionRegistryPostProcessor {

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {

        registry.registerBeanDefinition("myNewBean",
                BeanDefinitionBuilder
                        .genericBeanDefinition(MyNewInitializingBean.class)
                        .getBeanDefinition()
        );

    }

    @Override
    public void postProcessBeanFactory(
            @NonNull ConfigurableListableBeanFactory beanFactory) throws BeansException {

        if (beanFactory instanceof DefaultListableBeanFactory defaultListableBeanFactory) {
//            BeanDefinition beanDefinition1 = beanFactory.getBeanDefinition(NAME);
//            defaultListableBeanFactory.removeBeanDefinition(NAME);
        }
        for (String beanName : beanFactory.getBeanDefinitionNames()) {
            Class<?> clzzOfBean = beanFactory.getType(beanName);
            BeanDefinition beanDefinition = beanFactory.getBeanDefinition(beanName);
            IO.println(beanName + ":" + clzzOfBean.getName() + ":"
                    + beanDefinition.getDescription());
        }

    }
}
// 0. ingest (XML, component scanning, bean registrar, java config...)
// 1. BeanDefinition
// 2. beans

class MyBeanPostProcessor implements BeanPostProcessor {

    @Override
    public @Nullable Object postProcessAfterInitialization(
            Object bean, String beanName) throws BeansException {
        IO.println("postProcessAfterInitialization " + beanName);
        return bean;
    }
}

@Component(NAME)
class Bar {
    static final String NAME = "myBar";

    Bar() {
        IO.println("init'd " + NAME);
    }
}

class Foo implements InitializingBean, DisposableBean {

    private final Bar bar;

    Foo(String value, Bar bar) {
        this.bar = bar;
        IO.println(value);
    }

    @PostConstruct
        // InitializingBean
    void after() {
        IO.println("after");
    }

    @PreDestroy
        //DisposableBean
    void before() {
        IO.println("before");
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        IO.println("afterPropertiesSet");
    }

    @Override
    public void destroy() throws Exception {
        IO.println("destroy");
    }
}


