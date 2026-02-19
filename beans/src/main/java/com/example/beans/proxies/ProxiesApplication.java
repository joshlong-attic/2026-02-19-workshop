package com.example.beans.proxies;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.jspecify.annotations.Nullable;
import org.springframework.aop.framework.ProxyFactoryBean;
import org.springframework.beans.BeansException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.jdbc.support.JdbcTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import javax.sql.DataSource;
import java.lang.reflect.Method;

public class ProxiesApplication {

  /*  public static void main(String[] args) {

        var ac = new AnnotationConfigApplicationContext(MyConfig.class);
        var customerService = ac.getBean(JdbcCustomerService.class);
        var customer = customerService.getCustomerById(1);
        IO.println("> " + customer);

    }*/
}

@ComponentScan
@Configuration
class MyConfig {

    @Bean
    EmbeddedDatabase dataSource() {
        return new EmbeddedDatabaseBuilder()
                .setType(EmbeddedDatabaseType.H2)
                .build();
    }

    @Bean
    JdbcTransactionManager jdbcTransactionManager(DataSource dataSource) {
        return new JdbcTransactionManager(dataSource);
    }

    @Bean
    TransactionTemplate transactionTemplate(PlatformTransactionManager transactionManager) {
        return new TransactionTemplate(transactionManager);
    }

    @Bean
    JdbcCustomerService jdbcCustomerService() {
        return new JdbcCustomerService();
    }

    @Bean
    static TxBeanPostProcessor txBeanPostProcessor() {
        return new TxBeanPostProcessor();
    }

}

class TxBeanPostProcessor implements org.springframework.beans.factory.config.BeanPostProcessor {

    @Override
    public @Nullable Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof Tx) {
            return TxProxy.proxy(bean);
        }
        return bean;
    }
}

class TxProxy {

    static <T> T proxy(T t) {
        var pfb = new ProxyFactoryBean();
        pfb.setTarget(t);
        pfb.setProxyTargetClass(true);
        pfb.addAdvice(new MethodInterceptor() {
            @Override
            public @Nullable Object invoke(MethodInvocation invocation) throws Throwable {
                Method method = invocation.getMethod();
                Object[] args = invocation.getArguments();
                IO.println("start tx method: " + method.getName());
                Object result = method.invoke(t, args);
                IO.println("stop tx method: " + method.getName());
                return result;
            }
        });
        return (T) pfb.getObject();
    }

}

interface Tx {
}

//@Service
class JdbcCustomerService implements Tx {

    Customer getCustomerById(int id) {
        return new Customer(id, "name-" + id);
    }
}


record Customer(int id, String name) {
}