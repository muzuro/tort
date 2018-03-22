package com.mzr.tort.core.extractor;

import com.google.common.base.Joiner;
import com.mzr.tort.core.domain.IdentifiedEntity;
import com.mzr.tort.core.dto.IdentifiedDto;
import com.mzr.tort.core.dto.utils.DtoUtils;
import com.mzr.tort.core.dto.utils.Prop;
import com.mzr.tort.core.dto.utils.TypeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.*;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Fetch;
import javax.persistence.criteria.FetchParent;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Root;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class TortCriteriaBuilder<E extends IdentifiedEntity, D extends IdentifiedDto> {

    private final Logger logger = LoggerFactory.getLogger(TortCriteriaBuilder.class);
    private final Map<String, PropertyCriteria> criterias = new HashMap<>();
    private final Class<E> rootEntity;


    private final Class<D> dtoClass;
    private EntityManager entityManager;
    private CriteriaBuilder cb;

    TortCriteriaBuilder(Class<E> aEntityClass, Class<D> aDtoClass, EntityManager entityManager) {
        rootEntity = aEntityClass;
        dtoClass = aDtoClass;
        this.entityManager = entityManager;
    }

    CriteriaQuery<E> buildCriteria() {
        cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<E> criteria = cb.createQuery(rootEntity);
        Root<E> root = criteria.from(rootEntity);
        PropertyCriteria rootPropertyCriteria = new PropertyCriteria(root, "", rootEntity);
        criterias.put("", rootPropertyCriteria);
        addJoins(dtoClass, rootEntity, rootPropertyCriteria);
        return criteria;
    }

    //
//    /**
//     * @return all current joins in alphabetical order, useful for debug
//     */
//    public String getCriteriasHierarchy() {
//        List<String> toSort = new ArrayList<>(criterias.keySet());
//        java.util.Collections.sort(toSort);
//        return Joiner.on("\n").join(toSort);
//    }
//
    private void addJoins(Class<D> aDtoClass,
            Class<E> aEntityClass, PropertyCriteria parentPropertyCriteria) {
        for (Prop dtoProp : DtoUtils.getMappedProps(aDtoClass)) {
            Prop entityProp = DtoUtils.findProp(aEntityClass, dtoProp.getMappedName());
            if (entityProp == null) {
                if (logger.isTraceEnabled()) {
                    logger.trace("Not mapped property {}.{} should be annotated with @NotMapped",
                            aDtoClass.getSimpleName(), dtoProp.getMappedName());
                }
                continue;
            }

            String childPath = Joiner.on(".").join(parentPropertyCriteria.path, dtoProp.getMappedName());
            if (childPath.startsWith(".")) {
                //parent is root entityClass, path should be - child name
                childPath = dtoProp.getMappedName();
            }

            if (IdentifiedDto.class.isAssignableFrom(dtoProp.getPropertyType())) {

                Class<D> childDtoClass = (Class<D>) dtoProp.getPropertyType();
                Class<E> childEntityClass = (Class<E>) entityProp.getPropertyType();
                if (detectCycle(childEntityClass, childPath)) {
                    continue;
                }
                Fetch fetch = parentPropertyCriteria.from.fetch(dtoProp.getMappedName());
                PropertyCriteria childPropertyCriteria = new PropertyCriteria(fetch, childPath,
                        childEntityClass);
                criterias.put(childPath, childPropertyCriteria);
                addJoins(childDtoClass, childEntityClass, childPropertyCriteria);
            } else if (Collection.class.isAssignableFrom(dtoProp.getPropertyType())) {

                Class<D> childDtoClass = TypeUtils.getTypeArgument(dtoProp.getReadMethod().getGenericReturnType(),
                        Collection.class);
                Class<E> childEntityClass = TypeUtils.getTypeArgument(entityProp.getReadMethod()
                        .getGenericReturnType(), Collection.class);
                if (detectCycle(childEntityClass, childPath)) {
                    continue;
                }
                Fetch fetch = parentPropertyCriteria.from.fetch(dtoProp.getMappedName());
                PropertyCriteria childPropertyCriteria = new PropertyCriteria(fetch, childPath,
                        childEntityClass);
                criterias.put(childPath, childPropertyCriteria);
                addJoins(childDtoClass, childEntityClass, childPropertyCriteria);
            } /*else {
                // verify simple field owners are joined
                addParamJoins(childPath);
            }*/

//            String childPath = Joiner.on(".").join(parentPropertyCriteria.getPath(), dtoProp.getMappedName());
//            if (childPath.startsWith(".")) {
//                //parent is root entityClass, path should be - child name
//                childPath = dtoProp.getMappedName();
//            }


        }
    }

    /**
     * Если
     * 1. уже есть критерия для поля с этим классом и у этой критерии есть подкритерии.
     * 2. в пути образуется цикл из классов. Например у Customer есть коллекция stands,
     *   у Stand в свою очередь есть поле Customer.
     * При совпадении обоих условий - для этого поля исключается джоин.
     * @param aEntityClass класс сущности поля подкритерии
     * @param childPath путь к подкритерии
     * @return
     */
    private boolean detectCycle(Class<?> aEntityClass, String childPath) {
        PropertyCriteria selected = findPropertyCriteria(aEntityClass);
        if (selected == null) {
            return false;
        } else {
            Collection<PropertyCriteria> subCriterias = findSubCriterias(selected);
            return !subCriterias.isEmpty() && isClassCycle(aEntityClass, childPath);
        }
    }

    private boolean isClassCycle(Class<?> aEntityClass, String childPath) {
        boolean classCycle = false;
        Set<Class<?>> pathClasses = new HashSet<>();
        Class<?> parent = rootEntity;
        pathClasses.add(parent);

        for (String propName : childPath.split("\\.")) {
            Prop childProp = DtoUtils.findProp(parent, propName);
            parent = childProp.getPropertyType();
            if (Collection.class.isAssignableFrom(parent)) {
                parent = TypeUtils.getTypeArgument(childProp.getReadMethod().getGenericReturnType(), Collection.class);
            }
            if (!pathClasses.add(parent)) {
                classCycle = true;
            }
        }
        return classCycle;
    }

    private Collection<PropertyCriteria> findSubCriterias(PropertyCriteria selected) {
        List<PropertyCriteria> result = new ArrayList<>();
        criterias.values().forEach((pc) -> {
            String currentPath = pc.path;
            if (currentPath.startsWith(selected.path) && currentPath.length() > selected.path.length()) {
                result.add(pc);
            }
        });
        return result;
    }

    private PropertyCriteria findPropertyCriteria(Class<?> aEntityClass) {
        for (PropertyCriteria pc : criterias.values()) {
            if (Objects.equals(pc.entityClass, aEntityClass)) {
                return pc;
            }
        }
        return null;
    }
//
//    private Criteria addCollectionJoin(String aChildPath, Prop dtoProp, Prop entityProp) {
//        OneToMany oneToMany = entityProp.findAnnotation(OneToMany.class);
//        if (oneToMany != null && FetchType.LAZY.equals(oneToMany.fetch())) {
//            return getRootCriteria().getCriteria().createCriteria(aChildPath, CriteriaSpecification.LEFT_JOIN);
//        }
//        ManyToMany manyToMany = entityProp.findAnnotation(ManyToMany.class);
//        if (manyToMany != null && FetchType.LAZY.equals(manyToMany.fetch())) {
//            return getRootCriteria().getCriteria().createCriteria(aChildPath, CriteriaSpecification.LEFT_JOIN);
//        }
//        return null;
//    }
//
//    private Join addSingleJoin(String aChildPath, Prop dtoProp, Prop entityProp) {
//        Validate.isTrue(IdentifiedEntity.class.isAssignableFrom(entityProp.getPropertyType()));
//        OneToOne oneToOne = entityProp.findAnnotation(OneToOne.class);
//        if (oneToOne != null /* && FetchType.LAZY.equals(oneToOne.fetch()) */) {
//            return getRootCriteria().getFrom().join(aChildPath);
//        }
//        ManyToOne manyToOne = entityProp.findAnnotation(ManyToOne.class);
//        if (manyToOne != null /* && FetchType.LAZY.equals(manyToOne.fetch()) */) {
//            return getRootCriteria().getFrom().join(aChildPath);
//        }
//        return null;
//    }
//
    public static class PropertyCriteria<E extends IdentifiedEntity> {

        private final FetchParent from;
        private final String path;
        private final Class<E> entityClass;

        public PropertyCriteria(FetchParent from, String path, Class<E> entityClass) {
            this.from = from;
            this.path = path;
            this.entityClass = entityClass;
        }


    }
//
//    void addParam(Param param) {
//        Criteria criteria = findCriteria(param.getPath());
//        String path = param.getPath();
//        if (criteria == null) {
//            addParamJoins(path);
//        }
//        criteria = criterias.get(param.getPath()).getCriteria();
//        Validate.notNull(criteria, "Invalid path '%s' in fetch param", path);
//        if (param instanceof FetchParam) {
//            criteria.add(((FetchParam) param).getCriterion());
//        } else if (param instanceof InRefsFetchParam) {
//            InRefsFetchParam irfp = (InRefsFetchParam) param;
//            List<Object> collect = irfp.getIds().stream().map(id->session.load(irfp.getEntityClass(), id))
//                    .collect(Collectors.toList());
//            criteria.add(Restrictions.in(irfp.getPropertyName(), collect));
//        } else if (param instanceof OrderParam) {
//            criteria.addOrder(((OrderParam) param).getOrder());
//        } else if (param instanceof PageParam) {
//            criteria.setFirstResult(((PageParam) param).getFrom());
//            criteria.setMaxResults(((PageParam) param).getCount());
//        } else if (param instanceof SubqueryExistParam) {
//            SubqueryExistParam subPar = (SubqueryExistParam) param;
//            DetachedCriteria rootSubquery = DetachedCriteria.forClass(subPar.getSubqueryClass(), subPar.getAlias());
//            for (FetchParam subParam : subPar.getSubqueryParams()) {
//                createSubcriteriaJoin(subParam.getPath(), rootSubquery).add(subParam.getCriterion());
//            }
//            rootSubquery.add(Restrictions.eqProperty(subPar.getRootReference(), "root.id"));
//            rootSubquery.setProjection(Projections.id());
//            getRootCriteria().getCriteria().add(Subqueries.exists(rootSubquery));
//        } else if (param instanceof AliasParam) {
//            Validate.isInstanceOf(Subcriteria.class, criteria, "AliasParam path should be subcriteria");
//            ((Subcriteria)criteria).setAlias(((AliasParam) param).getAlias());
//
//        } else if (param instanceof CacheModeParam) {
//            CacheModeParam cacheModeParam = CacheModeParam.class.cast(param);
//            criteria.setCacheMode(cacheModeParam.getCacheMode());
//        }
//    }
//
//    private DetachedCriteria createSubcriteriaJoin(String aPath, DetachedCriteria aDc) {
//        String[] split = aPath.split("\\.");
//        DetachedCriteria currentCriteria = aDc;
//        for (String associationPath : split) {
//            currentCriteria = currentCriteria.createCriteria(associationPath);
//        }
//        return currentCriteria;
//    }
//
//    private void addParamJoins(String path) {
//        List<String> addJoinsPath = new ArrayList<>();
//        String[] split = path.split("\\.");
//        String parentPath = null;
//        for (String part : split) {
//            parentPath = Joiner.on(".").skipNulls().join(parentPath, part);
//            if (!criterias.containsKey(parentPath)) {
//                addJoinsPath.add(parentPath);
//            }
//        }
//        for (String fieldPath : addJoinsPath) {
//            Class<?> entityClass = getRootCriteria().getEntityClass();
//            Prop prop = DtoUtils.findProp(entityClass, fieldPath);
//            Validate.notNull(prop, "not found property %s in %s", fieldPath, entityClass);
//            if (IdentifiedEntity.class.isAssignableFrom(prop.getPropertyType())
//                    || Collection.class.isAssignableFrom(prop.getPropertyType())) {
//                Criteria createCriteria = getRootCriteria().getCriteria().createCriteria(fieldPath, CriteriaSpecification.LEFT_JOIN);
//                PropertyCriteria pc = new PropertyCriteria(createCriteria, fieldPath, null);
//                criterias.put(fieldPath, pc);
//            }
//        }
//    }
//
//    private PropertyCriteria getRootCriteria() {
//        return criterias.get("");
//    }
//
//    private String getParentPath(String aFieldName) {
//        int lastIndexOf = aFieldName.lastIndexOf('.');
//        return lastIndexOf != -1 ? aFieldName.substring(0, lastIndexOf) : "";
//    }
//
//    private Criteria findCriteria(String aPath) {
//        Criteria criteria = null;
//        for (PropertyCriteria pc : criterias.values()) {
//            if (Objects.equals(aPath, pc.getPath())) {
//                criteria = pc.getCriteria();
//            }
//        }
//        return criteria;
//    }
//
//    private Map<String, PropertyCriteria> getCriterias() {
//        return criterias;
//    }
//
//    protected Class<E> getRootEntity() {
//        return rootEntity;
//    }
//
//    protected Session getSession() {
//        return session;
//    }
//
//    protected Class<D> getDtoClass() {
//        return dtoClass;
//    }

}