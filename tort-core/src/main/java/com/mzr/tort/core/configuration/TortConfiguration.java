package com.mzr.tort.core.configuration;

import com.mzr.tort.core.dao.SimpleDao;
import com.mzr.tort.core.dao.SimpleDaoImpl;
import com.mzr.tort.core.extractor.DtoExtractor;
import com.mzr.tort.core.extractor.DtoExtractorImpl;
import com.mzr.tort.core.mapper.TortConfigurableMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TortConfiguration {

    @Bean
    public SimpleDao simpleDao() {
        return new SimpleDaoImpl();
    }

    @Bean
    public DtoExtractor dtoExtractor() {
        return new DtoExtractorImpl();
    }

    @Bean
    public TortConfigurableMapper tortConfigurableMapper() {
        return new TortConfigurableMapper();
    };

}
