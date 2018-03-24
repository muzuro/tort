package com.mzr.tort.core.extractor;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaQuery;

import com.mzr.tort.core.domain.IdentifiedEntity;
import com.mzr.tort.core.dto.IdentifiedDto;
import com.mzr.tort.core.extractor.param.Param;
import com.mzr.tort.core.mapper.TortConfigurableMapper;
import org.springframework.beans.factory.annotation.Autowired;

public class DtoExtractorImpl implements DtoExtractor {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private TortConfigurableMapper tortConfigurableMapper;

    @Override
    public <D extends IdentifiedDto, E extends IdentifiedEntity> TortCriteriaBuilder<E, D> extract(Class<D> aDtoClass, Class<E> aEntityClass) {
        return new TortCriteriaBuilder(aEntityClass, aDtoClass, entityManager)
                .mapper(tortConfigurableMapper)
                .buildCriteria();
    }

}
