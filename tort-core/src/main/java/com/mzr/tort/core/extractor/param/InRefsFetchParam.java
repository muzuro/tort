package com.mzr.tort.core.extractor.param;

import com.mzr.tort.core.domain.IdentifiedEntity;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.List;

/** Параметр для проверки, входит ли фк в коллекцию ид */
public class InRefsFetchParam extends Param {

    private final List<? extends Serializable> ids;
    private final Class<? extends IdentifiedEntity> entityClass;
    private final String propertyName;

    /**
     * Накладывается на корневую сущность
     * @param aIds список ид
     * @param aEntityClass класс сущности проперти
     * @param aPropertyName имя проперти
     */
    public InRefsFetchParam(List<? extends Serializable> aIds,
            Class<? extends IdentifiedEntity> aEntityClass, String aPropertyName) {
        super(StringUtils.EMPTY);
        ids = aIds;
        entityClass = aEntityClass;
        propertyName = aPropertyName;
    }
    
    /**
     * @param aPath путь от корневой сущности к сущности проверяймой проперти
     * @param aIds список ид
     * @param aEntityClass класс сущности проперти
     * @param aPropertyName имя проперти
     */
    public InRefsFetchParam(String aPath, List<? extends Serializable> aIds,
            Class<? extends IdentifiedEntity> aEntityClass, String aPropertyName) {
        super(aPath);
        ids = aIds;
        entityClass = aEntityClass;
        propertyName = aPropertyName;
    }

    public List<? extends Serializable> getIds() {
        return ids;
    }

    public Class<? extends IdentifiedEntity> getEntityClass() {
        return entityClass;
    }

    public String getPropertyName() {
        return propertyName;
    }
    
}
