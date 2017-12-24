package com.mzr.tort.core.dao;

import com.mzr.tort.core.domain.Finishable;
import com.mzr.tort.core.domain.IdentifiedEntity;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.transform.DistinctResultTransformer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class SimpleDaoImpl implements SimpleDao {

    @PersistenceContext
    private EntityManager entityManager;

    private Session getSession() {
        return entityManager.unwrap(Session.class);
    }

    @Override
    public <D> List<D> fetchAll(final Class<D> aClass) {
        CriteriaQuery<D> criteria = getSession().getCriteriaBuilder().createQuery(aClass);
        criteria.select(criteria.from(aClass));
        return getSession().createQuery(criteria).setResultTransformer(DistinctResultTransformer.INSTANCE).getResultList();
    }

    @Override
    public <D> D findById(Class<D> aClass, Serializable aId) {
        return Objects.nonNull(aId) ? getSession().get(aClass, aId) : null;
    }

    @Override
    public <D extends Serializable> List<D> getEntities(Class<D> entityClass, List<? extends Serializable> entityIds) {
        if (entityIds.isEmpty()) return Collections.emptyList();

        CriteriaBuilder cb = getSession().getCriteriaBuilder();
        CriteriaQuery<D> criteria = cb.createQuery(entityClass);
        Root<D> from = criteria.from(entityClass);
        criteria.where(from.get("id").in(entityIds));
        criteria.select(from);
        return getSession().createQuery(criteria).getResultList();
    }

    @Override
    public <E extends IdentifiedEntity> E loadById(Class<E> aClass, Serializable aId) {
        return Objects.nonNull(aId) ? getSession().load(aClass, aId) : null;
    }

    @Override
    public <D> D findByField(Class<D> aClass, String aFieldName, Object aValue) {
        CriteriaBuilder cb = getSession().getCriteriaBuilder();
        CriteriaQuery<D> criteria = cb.createQuery(aClass);
        Root<D> from = criteria.from(aClass);
        criteria.where(cb.equal(from.get(aFieldName), aValue));
        return getSession().createQuery(criteria).uniqueResult();
    }

    @Override
    public <D> List<D> fetchByField(Class<D> aClass, String aFieldName, Object aValue) {
        CriteriaBuilder cb = getSession().getCriteriaBuilder();
        CriteriaQuery<D> criteria = cb.createQuery(aClass);
        Root<D> from = criteria.from(aClass);
        criteria.where(cb.equal(from.get(aFieldName), aValue));
        return getSession().createQuery(criteria).getResultList();
    }

    @Override
    public <D> Long countByField(Class<D> aClass, String aFieldName, Object aValue) {
        CriteriaBuilder cb = getSession().getCriteriaBuilder();
        CriteriaQuery<Long> criteria = cb.createQuery(Long.class);
        Root<D> from = criteria.from(aClass);
        criteria.select(cb.count(from));
        criteria.where(cb.equal(from.get(aFieldName), aValue));
        return getSession().createQuery(criteria).uniqueResult();
    }

    @Override
    @Transactional
    public <D extends IdentifiedEntity> Serializable save(D aEntity) {
        return getSession().save(aEntity);
    }

    @Override
    @Transactional
    public <E extends IdentifiedEntity> void saveOrUpdate(E aEntity) {
        getSession().saveOrUpdate(aEntity);
    }

    @Override
    @Transactional
    public <E extends IdentifiedEntity> void update(E aEntity) {
        getSession().update(aEntity);
    }

    @Override
    @Transactional
    public <E extends IdentifiedEntity> void refresh(E aEntity) {
        getSession().refresh(aEntity);
    }

    @Override
    public <E extends IdentifiedEntity> void forceUpdate(E aEntity) {
//        entityManager.detach(aEntity);
        System.out.println(entityManager.getLockMode(aEntity));
        entityManager.lock(aEntity, LockModeType.OPTIMISTIC_FORCE_INCREMENT);
        System.out.println(entityManager.getLockMode(aEntity));
        entityManager.merge(aEntity);
        System.out.println(entityManager.getLockMode(aEntity));
//        entityManager.merge()
    }

    @Override
    @Transactional
    public <E extends IdentifiedEntity> void delete(E aEntity) {
        getSession().delete(aEntity);
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public <E extends IdentifiedEntity> void evict(E aEntity) {
        getSession().evict(aEntity);
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public void flush() {
        getSession().flush();
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public void clearSession() {
        getSession().clear();
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public boolean sessionContains(Object aObject) {
        return getSession().contains(aObject);
    }

    @Override
    public <T extends Finishable> List<T> fetchAllNotFinished(Class<T> aClass) {
        CriteriaBuilder cb = getSession().getCriteriaBuilder();
        CriteriaQuery<T> criteria = cb.createQuery(aClass);
        Root<T> from = criteria.from(aClass);
        criteria.where(cb.isNull(from.get("finishTime")));
        return getSession().createQuery(criteria).getResultList();
    }
}
