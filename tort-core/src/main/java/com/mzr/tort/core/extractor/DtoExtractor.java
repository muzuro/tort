package com.mzr.tort.core.extractor;

import com.mzr.tort.core.extractor.param.Param;
import org.hibernate.CacheMode;
import org.hibernate.FlushMode;
import org.hibernate.NonUniqueResultException;
import com.mzr.tort.core.domain.IdentifiedEntity;
import com.mzr.tort.core.dto.IdentifiedDto;

import java.io.Serializable;
import java.util.List;
import java.util.function.BiConsumer;

/**
 */
public interface DtoExtractor {

//    <D extends IdentifiedDto, E extends IdentifiedEntity> E find(Class<D> aDtoClass, Class<E> aEntityClass, Serializable aId);
//
//    <D extends IdentifiedDto, E extends IdentifiedEntity> E find(Class<D> aDtoClass, Class<E> aEntityClass, Param... aFetchParams);
//
//    /**
//     * @return E or null.
//     * @throws NonUniqueResultException
//     */
//    <E extends IdentifiedEntity> E find(Class<E> aEntityClass, Param... aFetchParams);
//
//    /**
//     * @return E or null.
//     * @throws NonUniqueResultException
//     */
//    <E extends IdentifiedEntity> E find(Class<E> aEntityClass, List<Param> aParams);
//
//    <D extends IdentifiedDto, E extends IdentifiedEntity> D findDto(Class<D> aDtoClass, Class<E> aEntityClass, Serializable aId);
//
//    <E extends IdentifiedEntity> E find(Class<E> aEntityClass, Serializable aId);
//
//    /**
//     * @return D or null (depends on how TortConfigurableMapper handles nulls).
//     * @throws NonUniqueResultException
//     */
//    <D extends IdentifiedDto, E extends IdentifiedEntity> D findDto(Class<D> aDtoClass, Class<E> aEntityClass, Param... aFetchParams);
//
//    <E extends IdentifiedEntity> List<E> fetch(Class<E> aEntityClass, FlushMode aFlushMode, Param... aParams);
//
//    <E extends IdentifiedEntity> List<E> fetch(Class<E> aEntityClass, Param... aParams);
//
//    <E extends IdentifiedEntity> List<E> fetch(Class<E> aEntityClass, List<Param> aParams);
//
//    <D extends IdentifiedDto, E extends IdentifiedEntity> List<E> fetch(Class<D> aDtoClass, Class<E> aEntityClass, Param... aParams);
//
//    <D extends IdentifiedDto, E extends IdentifiedEntity> List<E> fetch(Class<D> aDtoClass, Class<E> aEntityClass, List<Param> aParams);
//
//    <D extends IdentifiedDto, E extends IdentifiedEntity> List<E> fetch(Class<D> aDtoClass, Class<E> aEntityClass, CacheMode cacheMode, List<Param> aParams);
//
//    <D extends IdentifiedDto, E extends IdentifiedEntity> List<D> fetchDto(Class<D> aDtoClass, Class<E> aEntityClass, Param... aParams);
//
//    <D extends IdentifiedDto, E extends IdentifiedEntity> List<D> fetchDto(Class<D> aDtoClass, Class<E> aEntityClass, CacheMode cacheMode, Param... aParams);
//
//    <D extends IdentifiedDto, E extends IdentifiedEntity> List<D> fetchDto(Class<D> aDtoClass, Class<E> aEntityClass, List<Param> aParams);
//
//    /**
//     * @param aDtoClass
//     * @param aEntityClass
//     * @param aPostMapper - калбек для домапливания
//     * @param aParams
//     * @return
//     */
//    <D extends IdentifiedDto, E extends IdentifiedEntity> List<D> fetchDto(Class<D> aDtoClass, Class<E> aEntityClass,
//            BiConsumer<D, E> aPostMapper, List<Param> aParams);
//
//    <E extends IdentifiedEntity> long calculateCount(Class<E> aEntityClass, Param... aParams);
//
//    <E extends IdentifiedEntity> long calculateCount(Class<E> aEntityClass, List<Param> aParams);
//
//    <E extends IdentifiedEntity> long calculateDistinctCount(Class<E> aEntityClass, List<Param> aParams);
//
//    <E extends IdentifiedEntity> List<Serializable> fetchIds(Class<E> aEntityClass, Param... aParams);
//
//    <E extends IdentifiedEntity> List<Serializable> fetchIds(Class<E> aEntityClass, List<Param> aParams);
//
//    <E extends IdentifiedEntity> List<Serializable> fetchDistinctIds(Class<E> aEntityClass, List<Param> aParams);

}
