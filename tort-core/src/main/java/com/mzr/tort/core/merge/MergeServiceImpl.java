package com.mzr.tort.core.merge;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.mzr.tort.core.domain.IdentifiedEntity;
import com.mzr.tort.core.dto.IdentifiedDto;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;

public class MergeServiceImpl implements MergeService {

    private SessionFactory sessionFactory;
    
    @Resource(name="sessionFactory")
    @Override
    public void setSessionFactory(SessionFactory aSessionFactory) {
        sessionFactory = aSessionFactory;
    }
    
    @Override
    @Transactional
    public <D extends IdentifiedDto, E extends IdentifiedEntity> MergeCollectionResult<E> mergeCollectionRefs(Collection<D> aDtos,
            Collection<E> aEntities, Class<E> aEntityClass) {
        Set<E> deleted = removeCollectionRefs(aDtos, aEntities);
        Set<E> added = new HashSet<>();
        Set<E> remaining = new HashSet<>();
        for (final D srcItem : aDtos) {
            E destItem = findEntity(aEntities, srcItem);
            if (destItem == null) {
                destItem = (E) sessionFactory.getCurrentSession().get(aEntityClass, srcItem.getId());
                aEntities.add(destItem);
                added.add(destItem);
            } else {
                remaining.add(destItem);
            }
        }
        return new MergeCollectionResult<>(added, deleted, remaining);
    }
    
    @Override
    @Transactional
    public <D extends IdentifiedDto, E extends IdentifiedEntity> MergeCollectionResult<E> mergeCollectionRefs(Collection<D> aDtos,
            Collection<E> aEntities, Class<E> aEntityClass, Updater<D, E> aUpdater, Deleter<E> aDeleter) {
        Set<E> deleted = removeCollectionRefs(aDtos, aEntities, aDeleter);
        Set<E> added = new HashSet<>();
        Set<E> remaining = new HashSet<>();
        for (final D srcItem : aDtos) {
            E destItem = findEntity(aEntities, srcItem);
            if (destItem == null) {
                if (srcItem.getId() != null) {
                    //элемент коллекции находиться в другой коллекции
                    destItem = (E) sessionFactory.getCurrentSession().get(aEntityClass, srcItem.getId());
                    aUpdater.linkToParent(destItem);
                } else {                    
                    //элемента коллекции нет в БД - создаём новый инстанс
                    try {
                        destItem = aEntityClass.newInstance();
                        aUpdater.linkToParent(destItem);
                    } catch (InstantiationException | IllegalAccessException e) {
                        throw new IllegalStateException(e);
                    }
                }
                added.add(destItem);
            } else {
                remaining.add(destItem);
            }
            aUpdater.update(srcItem, destItem);
        }
        return new MergeCollectionResult<>(added, deleted, remaining);
    }

    @Override
    @Transactional
    public <D extends IdentifiedDto, E extends IdentifiedEntity> MergeCollectionResult<E> mergeCollectionRefsNoCreate(Collection<D> aDtos,
            Collection<E> aEntities, Class<E> aEntityClass, Updater<D, E> aUpdater, Deleter<E> aDeleter) {
        Set<E> deleted = removeCollectionRefs(aDtos, aEntities, aDeleter);
        Set<E> added = new HashSet<>();
        Set<E> remaining = new HashSet<>();
        for (final D srcItem : aDtos) {
            E destItem = findEntity(aEntities, srcItem);
            if (destItem == null) {
                if (srcItem.getId() != null) {
                    //элемент коллекции находиться в другой коллекции
                    destItem = (E) sessionFactory.getCurrentSession().get(aEntityClass, srcItem.getId());
                    aUpdater.linkToParent(destItem);
                } else {
                    //элемента коллекции нет в БД - явно выдаю ошибку.
                    throw new IllegalArgumentException("Creating new items is not allowed.");
                }
                added.add(destItem);
            } else {
                remaining.add(destItem);
            }
            aUpdater.update(srcItem, destItem);
        }
        return new MergeCollectionResult<>(added, deleted, remaining);
    }

    @Override
    @Transactional
    public <D extends IdentifiedDto, E extends IdentifiedEntity> MergeCollectionResult<E> mergeCollectionRefs(Collection<D> aDtos,
            Collection<E> aEntities, Class<E> aEntityClass, Updater<D, E> aUpdater) {
        return mergeCollectionRefs(aDtos, aEntities, aEntityClass, aUpdater, e->{});
    }

    @Override
    @Transactional
    public <D extends IdentifiedDto, E extends IdentifiedEntity> Set<E> removeCollectionRefs(Collection<D> aDtos,
            Collection<E> aEntities) {
        return removeCollectionRefs(aDtos, aEntities, e->{});
    }
    
    @Override
    @Transactional
    public <D extends IdentifiedDto, E extends IdentifiedEntity> Set<E> removeCollectionRefs(Collection<D> aDtos,
            Collection<E> aEntities, Deleter<E> aDeleter) {
        Set<E> removed = new HashSet<>();
        Iterator<E> entitiesIterator = aEntities.iterator();
        while (entitiesIterator.hasNext()) {
            final E entity = entitiesIterator.next();

            D srcItem = Iterables.find(
                    aDtos,
                    new Predicate<D>() {
                        @Override
                        public boolean apply(D dto) {
                            return dto != null && Objects.equals(dto.getId(), entity.getId());
                        }
                    },
                    null
            );
            if (srcItem == null) {
                removed.add(entity);
                entitiesIterator.remove();
                aDeleter.delete(entity);
            }
        }
        return removed;
    }

    @Override
    @Transactional
    public <D extends IdentifiedDto, E extends IdentifiedEntity> E findEntity(Collection<E> aEntities, final D aDto) {
        E destItem = Iterables.find(aEntities, new Predicate<E>() {
            @Override
            public boolean apply(E input) {
                return input != null && input.getId() != null && input.getId().equals(aDto.getId());
            }
        }, null);
        return destItem;
    }

}
