package com.mzr.tort.core.configuration;

import com.mzr.tort.core.dao.SimpleDao;
import com.mzr.tort.core.dao.SimpleDaoImpl;
import com.mzr.tort.core.extractor.DtoExtractor;
import com.mzr.tort.core.extractor.DtoExtractorImpl;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TortConfiguration {

    @Bean
    public SimpleDao getSimpleDao() {
        return new SimpleDaoImpl();
    }

    @Bean
    public DtoExtractor getDtoExtractor() {
        return new DtoExtractorImpl();
    }

}
