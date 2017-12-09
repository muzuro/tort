package com.mzr.tort.testsample;

import com.mzr.tort.core.dao.SimpleDao;
import com.mzr.tort.core.dao.SimpleDaoImpl;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.mzr.tort"})
public class TestApplication {
    public static void main(String[] args) {
        SpringApplication.run(TestApplication.class, args);
    }
    @Bean
    public SimpleDao getSimpleDao() {
        return new SimpleDaoImpl();
    }
}
