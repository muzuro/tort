package com.mzr.tort.testsample;

import com.mzr.tort.core.dao.SimpleDao;
import com.mzr.tort.core.dao.SimpleDaoImpl;
import com.mzr.tort.core.extractor.DtoExtractor;
import com.mzr.tort.core.extractor.DtoExtractorImpl;
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

    @Bean
    public DtoExtractor getDtoExtractor() {
        return new DtoExtractorImpl();
    }

}
