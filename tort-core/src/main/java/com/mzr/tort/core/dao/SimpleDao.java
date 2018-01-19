package com.mzr.tort.core.dao;

import com.mzr.tort.core.domain.Finishable;
import com.mzr.tort.core.domain.IdentifiedEntity;

import java.io.Serializable;
import java.util.List;

/**
 */
public interface SimpleDao {

    <D> D findById(Class<D> aClass, Serializable aId);

    <D extends Serializable> List<D> getEntities(Class<D> entityClass, List<? extends Serializable> entityIds);

    <D> List<D> fetchAll(Class<D> aClass);

    <D> D findByField(Class<D> aClass, String aFieldName, Object value);

    <D> List<D> fetchByField(Class<D> aClass, String aFieldName, Object value);

    <E extends IdentifiedEntity> E loadById(Class<E> aClass, Serializable aId);

    <D> Long countByField(Class<D> aClass, String aFieldName, Object aValue);

    /**
     * @param aEntity
     * @return
     */
    <D extends IdentifiedEntity> Serializable save(D aEntity);

    /**
     */
    <E extends IdentifiedEntity> void saveOrUpdate(E aEntity);

    <E extends IdentifiedEntity> void update(E aEntity);

    <E extends IdentifiedEntity> void refresh(E aEntity);

    /**
     * force update entity version
     * @param aEntity
     */
    <E extends IdentifiedEntity> void forceUpdate(E aEntity);

    <E extends IdentifiedEntity> void delete(E aEntity);

    <E extends IdentifiedEntity> void evict(E aEntity);

    void flush();

    void clearSession();

    /**
     * Получить все экземпляры сущности, у которых поле finishTime = null.
     */
    <T extends Finishable> List<T> fetchAllNotFinished(Class<T> clazz);

    /**
     * @param aObject
     * @return
     */
    boolean sessionContains(Object aObject);
}
