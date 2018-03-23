package com.mzr.tort.core.extractor;

import com.mzr.tort.core.domain.IdentifiedEntity;
import com.mzr.tort.core.dto.IdentifiedDto;

public interface DtoExtractor {

    <D extends IdentifiedDto, E extends IdentifiedEntity> TortCriteriaBuilder<E, D> extract(Class<D> aDtoClass,
            Class<E> aEntityClass);

}
