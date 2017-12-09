package com.mzr.tort.core.extractor.criteria;

import com.mzr.tort.core.domain.IdentifiedEntity;
import com.mzr.tort.core.dto.IdentifiedDto;
import com.mzr.tort.core.extractor.param.Param;
import org.hibernate.CacheMode;
import org.hibernate.Criteria;
import org.hibernate.FlushMode;
import org.hibernate.NonUniqueResultException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;


import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.DistinctRootEntityResultTransformer;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

public class DtoDao {

//    @Autowired
//    private SessionFactory sessionFactory;
//
//    private Session getSession() {
//        return sessionFactory.getCurrentSession();
//    }
//
//    public <E extends IdentifiedEntity, D extends IdentifiedDto> E find(Class<E> aEntityClass, Class<D> aDtoClass,
//                                                                        Serializable aId) {
//        Criteria root = new TortCriteriaBuilder<E, D>(aEntityClass, aDtoClass, getSession()).buildCriteria();
//        return (E) root.add(Restrictions.idEq(aId)).uniqueResult();
//    }
//
//    public <E extends IdentifiedEntity, D extends IdentifiedDto> List<E> fetch(Class<E> aEntityClass,
//            Class<D> aDtoClass, Param... aParams) {
//        return fetch(aEntityClass, aDtoClass, (CacheMode)null, aParams);
//    }
//
//    public <E extends IdentifiedEntity, D extends IdentifiedDto> List<E> fetch(Class<E> aEntityClass,
//            Class<D> aDtoClass, CacheMode aCacheMode, Param... aParams) {
//        TortCriteriaBuilder<E, D> tortCriteriaBuilder = new TortCriteriaBuilder<>(aEntityClass, aDtoClass, getSession());
//        Criteria root = tortCriteriaBuilder.buildCriteria();
//        for (Param param : aParams) {
//            tortCriteriaBuilder.addParam(param);
//        }
//        if (Objects.nonNull(aCacheMode)) {
//            root.setCacheMode(aCacheMode);
//        }
//        return root.setResultTransformer(DistinctRootEntityResultTransformer.INSTANCE).list();
//    }
//
//    private <E extends IdentifiedEntity, D extends IdentifiedDto> List<E> fetch(Class<E> aEntityClass,
//            Class<D> aDtoClass, FlushMode aFlushMode, Param[] aParams) {
//        TortCriteriaBuilder<E, D> tortCriteriaBuilder = new TortCriteriaBuilder<>(aEntityClass, aDtoClass, getSession());
//        Criteria root = tortCriteriaBuilder.buildCriteria();
//        for (Param param : aParams) {
//            tortCriteriaBuilder.addParam(param);
//        }
//        if (Objects.nonNull(aFlushMode)) {
//            root.setFlushMode(aFlushMode);
//        }
//        return root.setResultTransformer(DistinctRootEntityResultTransformer.INSTANCE).list();
//    }
//
//    public <E extends IdentifiedEntity> List<E> fetch(Class<E> aEntityClass,
//            Param... aParams) {
//        return fetch(aEntityClass, IdentifiedDto.class, aParams);
//    }
//
//    public <E extends IdentifiedEntity> List<E> fetch(Class<E> aEntityClass, FlushMode aFlushMode, Param[] aParams) {
//        return fetch(aEntityClass, IdentifiedDto.class, aFlushMode, aParams);
//    }
//
//    public <E extends IdentifiedEntity> E findByFetch(Class<E> aEntityClass, Param... aFetchParams) {
//        return findByFetch(aEntityClass, IdentifiedDto.class, aFetchParams);
//    }
//
//    public <E extends IdentifiedEntity, D extends IdentifiedDto> E findByFetch(Class<E> aEntityClass,
//            Class<D> aDtoClass, Param... aFetchParams) {
//        TortCriteriaBuilder<E, D> tortCriteriaBuilder = new TortCriteriaBuilder<>(aEntityClass, aDtoClass, getSession());
//        Criteria root = tortCriteriaBuilder.buildCriteria();
//        for (Param param : aFetchParams) {
//            tortCriteriaBuilder.addParam(param);
//        }
//        List<E> list = root.setResultTransformer(DistinctRootEntityResultTransformer.INSTANCE).list();
//        if (list.size() > 1) {
//            throw new NonUniqueResultException(list.size());
//        }
//        return list.size() == 1 ? list.get(0) : null;
//    }
//
//    public <E extends IdentifiedEntity> E findByParam(Class<E> aEntityClass, Param... aFetchParams) {
//        return findByFetch(aEntityClass, IdentifiedDto.class, aFetchParams);
//    }
//
//    public <E extends IdentifiedEntity> long calculateCount(Class<E> aEntityClass, Param... aParams) {
//        TortCriteriaBuilder<E, IdentifiedDto> tortCriteriaBuilder = new TortCriteriaBuilder<>(aEntityClass, IdentifiedDto.class,
//                getSession());
//        Criteria root = tortCriteriaBuilder.buildCriteria();
//        for (Param param : aParams) {
//            tortCriteriaBuilder.addParam(param);
//        }
//        return (Long) root.setProjection(Projections.countDistinct("id")).uniqueResult();
//    }
//
//    public <E extends IdentifiedEntity> long calculateDistinctCount(Class<E> aEntityClass, Param... aParams) {
//        TortCriteriaBuilder<E, IdentifiedDto> tortCriteriaBuilder = new TortCriteriaBuilder<>(aEntityClass,
//                IdentifiedDto.class, getSession());
//        Criteria root = tortCriteriaBuilder.buildCriteria();
//        for (Param param : aParams) {
//            tortCriteriaBuilder.addParam(param);
//        }
//        return (Long) root
//                .setProjection(Projections.countDistinct("id"))
//                .uniqueResult();
//    }
//
//    public <E extends IdentifiedEntity> List<Serializable> fetchIds(Class<E> aEntityClass, Param... aParams) {
//        TortCriteriaBuilder<E, IdentifiedDto> tortCriteriaBuilder = new TortCriteriaBuilder<>(aEntityClass,
//                IdentifiedDto.class, getSession());
//        Criteria root = tortCriteriaBuilder.buildCriteria();
//        for (Param param : aParams) {
//            tortCriteriaBuilder.addParam(param);
//        }
//        return (List<Serializable>) root.setProjection(Projections.property("id")).list();
//    }
//
//    public <E extends IdentifiedEntity> List<Serializable> fetchIds(Class<E> aEntityClass, List<Param> aParams) {
//        TortCriteriaBuilder<E, IdentifiedDto> tortCriteriaBuilder = new TortCriteriaBuilder<>(aEntityClass, IdentifiedDto.class,
//                getSession());
//        Criteria root = tortCriteriaBuilder.buildCriteria();
//        for (Param param : aParams) {
//            tortCriteriaBuilder.addParam(param);
//        }
//        return (List<Serializable>) root.setProjection(Projections.property("id")).list();
//    }
//
//    public <E extends IdentifiedEntity> List<Serializable> fetchDistinctIds(Class<E> aEntityClass,
//            List<Param> aParams) {
//        TortCriteriaBuilder<E, IdentifiedDto> tortCriteriaBuilder = new TortCriteriaBuilder<>(aEntityClass, IdentifiedDto.class,
//                getSession());
//        Criteria root = tortCriteriaBuilder.buildCriteria();
//        for (Param param : aParams) {
//            tortCriteriaBuilder.addParam(param);
//        }
//        return (List<Serializable>) root.setProjection(Projections.distinct(Projections.property("id")))
//                .list();
//    }

}
