package com.mzr.tort.core.merge;

import org.hibernate.SessionFactory;

import com.mzr.tort.core.domain.IdentifiedEntity;
import com.mzr.tort.core.dto.IdentifiedDto;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

/**
 * Сервис для упрощения мерджа коллекций в сущность(из дто в сущность)
 */
public interface MergeService {

    /**
     * @param aSessionFactory
     */
    void setSessionFactory(SessionFactory aSessionFactory);
    
    /**
     * Удаляет удалённые из коллекции дто, и добавляет добавленные в коллекции дто
     * @param aDtos коллекция дто
     * @param aEntities коллекция сущностей
     * @param aEntityClass класс сущности, которая находится в коллекции сущностей
     */
    <D extends IdentifiedDto, E extends IdentifiedEntity> MergeCollectionResult<E> mergeCollectionRefs(Collection<D> aDtos, Collection<E> aEntities,
            Class<E> aEntityClass);
    
    /**
     * Удаляет удалённые из коллекции дто, добавляет добавленные в коллекции дто
     * @param aDtos коллекция дто
     * @param aEntities коллекция сущностей
     * @param aEntityClass класс сущности, которая находится в коллекции сущностей
     * @param aUpdater коллбек обновления сущностей
     */
    <D extends IdentifiedDto, E extends IdentifiedEntity> MergeCollectionResult<E> mergeCollectionRefs(Collection<D> aDtos, Collection<E> aEntities,
            Class<E> aEntityClass, Updater<D, E> aUpdater);
    
    /**
     * Удаляет удалённые из коллекции дто, добавляет добавленные в коллекции дто
     * @param aDtos коллекция дто
     * @param aEntities коллекция сущностей
     * @param aEntityClass класс сущности, которая находится в коллекции сущностей
     * @param aUpdater коллбек в котором имплементируется обновление сущностей
     * @param aDeleter коллбек который исполняется вместе с удалением сущности из коллекции
     */
    <D extends IdentifiedDto, E extends IdentifiedEntity> MergeCollectionResult<E> mergeCollectionRefs(Collection<D> aDtos, Collection<E> aEntities,
            Class<E> aEntityClass, Updater<D, E> aUpdater, Deleter<E> aDeleter);

    /**
     * По большей части это копия метода mergeCollectionRefs, но здесь явно запрещено создание новых объектов.
     * Т.е. объект должен быть в базе, чтобы мерджиться в коллекцию. И его ID <b>должно</b> быть указано в dto.
     *
     * @param aDtos
     * @param aEntities
     * @param aEntityClass
     * @param aUpdater
     * @param aDeleter
     * @param <D>
     * @param <E>
     * @return
     */
    <D extends IdentifiedDto, E extends IdentifiedEntity> MergeCollectionResult<E> mergeCollectionRefsNoCreate(Collection<D> aDtos, Collection<E> aEntities,
            Class<E> aEntityClass, Updater<D, E> aUpdater, Deleter<E> aDeleter);

    /**
     * Удаляет удалённые из коллекции дто
     * @param aDtos коллекция дто
     * @param aEntities коллекция сущностей
     */
    <D extends IdentifiedDto, E extends IdentifiedEntity> Set<E> removeCollectionRefs(Collection<D> aDtos,
            Collection<E> aEntities);
    
    /**
     * Удаляет удалённые из коллекции дто
     * @param aDtos коллекция дто
     * @param aEntities коллекция сущностей
     */
    <D extends IdentifiedDto, E extends IdentifiedEntity> Set<E> removeCollectionRefs(Collection<D> aDtos,
            Collection<E> aEntities, Deleter<E> aDeleter);
    
    /**
     * Находит сущность в коллекции 
     * @param aEntities коллекция сущностей
     * @param aDto дто для которой ищеться сущность
     * @return
     */
    <D extends IdentifiedDto, E extends IdentifiedEntity> E findEntity(Collection<E> aEntities, final D aDto);

    /**
     * Интерфейс реализующий обновление сущности при мердже.
     * @param <D>
     * @param <E>
     */
    public static interface Updater<D extends IdentifiedDto, E extends IdentifiedEntity> {
        
        /**
         * Обновление элеметна коллекции
         * @param aDto
         * @param aEntity
         */
        void update(D aDto, E aEntity);
        
        /**
         * Добавление нового элемента в коллекцию
         * @param aNewElement
         */
        void linkToParent(E aNewElement);
        
    }
    
    /**
     * Интерфейс выполняющийся при удалении сущности из коллекции. Вместе с самим удалением. 
     * @param <D>
     * @param <E>
     */
    public static interface Deleter<E extends IdentifiedEntity> {
        void delete(E aEntity);
    }
    
    public static class MergeCollectionResult<E extends IdentifiedEntity> {
        private final Set<E> added;
        private final Set<E> deleted;
        private final Set<E> remaining;
        public MergeCollectionResult(Set<E> aAdded, Set<E> aDeleted, Set<E> aRemaining) {
            added = Collections.unmodifiableSet(aAdded);
            deleted = Collections.unmodifiableSet(aDeleted);
            remaining = Collections.unmodifiableSet(aRemaining);
        }
        public Set<E> getAdded() {
            return added;
        }
        public Set<E> getDeleted() {
            return deleted;
        }
        public Set<E> getRemaining() {
            return remaining;
        }
    }

}
