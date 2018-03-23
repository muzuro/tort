package com.mzr.tort.core.extractor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.FetchParent;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;

import com.google.common.base.Joiner;
import com.mzr.tort.core.domain.IdentifiedEntity;
import com.mzr.tort.core.dto.IdentifiedDto;
import com.mzr.tort.core.dto.utils.DtoUtils;
import com.mzr.tort.core.dto.utils.Prop;
import com.mzr.tort.core.dto.utils.TypeUtils;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TortCriteriaBuilder<E extends IdentifiedEntity, D extends IdentifiedDto> {

    private final Logger logger = LoggerFactory.getLogger(TortCriteriaBuilder.class);
    private final Map<String, PropertyMeta> criterias = new HashMap<>();
    private final Class<E> rootEntity;


    private final Class<D> dtoClass;
    private EntityManager entityManager;
    private CriteriaBuilder cb;
    private Root<E> root;
    private CriteriaQuery<E> criteriaQuery;

    TortCriteriaBuilder(Class<E> aEntityClass, Class<D> aDtoClass, EntityManager entityManager) {
        rootEntity = aEntityClass;
        dtoClass = aDtoClass;
        this.entityManager = entityManager;
    }

    TortCriteriaBuilder buildCriteria() {
        cb = entityManager.getCriteriaBuilder();
        criteriaQuery = cb.createQuery(rootEntity);
        root = criteriaQuery.from(rootEntity);
        PropertyMeta rootPropertyCriteria = new PropertyMeta(root, "", rootEntity);
        criterias.put("", rootPropertyCriteria);
        addJoins(dtoClass, rootEntity, rootPropertyCriteria);
        return this;
    }

    public TortCriteriaBuilder order(String aPath, String aFieldName, OrderType aOrderType) {
        List<Order> orders = new ArrayList<>();
        orders.addAll(criteriaQuery.getOrderList());

        PropertyMeta propertyMeta = criterias.get(aPath);
        Path path = propertyMeta.from.get(aFieldName);

        Order order;
        if (aOrderType == OrderType.ASC) {
            order = cb.asc(path);
        } else {
            order = cb.desc(path);
        }

        orders.add(order);
        criteriaQuery.orderBy(orders);
        return this;
    }

    /**
     * @return all current joins in alphabetical order, useful for debug
     */
    protected String getCriteriasHierarchy() {
        List<String> toSort = new ArrayList<>(criterias.keySet());
        java.util.Collections.sort(toSort);
        return Joiner.on("\n").join(toSort);
    }

    private void addJoins(Class<D> aDtoClass,
            Class<E> aEntityClass, PropertyMeta parentPropertyCriteria) {
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
                From fetch = (From) parentPropertyCriteria.from.fetch(dtoProp.getMappedName());
                PropertyMeta childPropertyCriteria = new PropertyMeta(fetch, childPath,
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
                From fetch = (From) parentPropertyCriteria.from.fetch(dtoProp.getMappedName());
                PropertyMeta childPropertyCriteria = new PropertyMeta(fetch, childPath,
                        childEntityClass);
                criterias.put(childPath, childPropertyCriteria);
                addJoins(childDtoClass, childEntityClass, childPropertyCriteria);
            } /*else {
                // verify simple field owners are joined
                addFilterJoins(childPath);
            }*/
        }
    }

    /**
     * If
     * 1. There is fetch for this field and this fetch has subfetch.
     * 2. There is cycle in class path
     * If both is true - we exclude fetch join
     * @param aEntityClass join entity class
     * @param childPath join path
     * @return
     */
    private boolean detectCycle(Class<?> aEntityClass, String childPath) {
        PropertyMeta selected = findPropertyCriteria(aEntityClass);
        if (selected == null) {
            return false;
        } else {
            Collection<PropertyMeta> subCriterias = findSubCriterias(selected);
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

    private Collection<PropertyMeta> findSubCriterias(PropertyMeta selected) {
        List<PropertyMeta> result = new ArrayList<>();
        criterias.values().forEach((pc) -> {
            String currentPath = pc.path;
            if (currentPath.startsWith(selected.path) && currentPath.length() > selected.path.length()) {
                result.add(pc);
            }
        });
        return result;
    }

    private PropertyMeta findPropertyCriteria(Class<?> aEntityClass) {
        for (PropertyMeta pc : criterias.values()) {
            if (Objects.equals(pc.entityClass, aEntityClass)) {
                return pc;
            }
        }
        return null;
    }

    public static class PropertyMeta<E extends IdentifiedEntity> {

        private final From from;
        private final String path;
        private final Class<E> entityClass;

        public PropertyMeta(From from, String path, Class<E> entityClass) {
            this.from = from;
            this.path = path;
            this.entityClass = entityClass;
        }
    }

    public TortCriteriaBuilder filter(String path, BiConsumer<CriteriaBuilder, FetchParent> criterion) {
        From join = findOrCreateJoin(path);
        criterion.accept(cb, join);
        return this;
    }

    public List<E> list() {
        TypedQuery<E> query = entityManager.createQuery(criteriaQuery);
        return query.getResultList();
    }

    public List<E> list(int aStartFrom, int aCount) {
        TypedQuery<E> query = entityManager.createQuery(criteriaQuery);
        query.setFirstResult(aStartFrom);
        query.setMaxResults(aCount);
        return query.getResultList();
    }

    public E unique() {
        TypedQuery<E> query = entityManager.createQuery(criteriaQuery);
        return query.getSingleResult();
    }

    private From findOrCreateJoin(String path) {
        From join = findJoin(path);
        if (join == null) {
            join = addFilterJoins(path).from;
        }
        return join;
    }

    /*void addParam(Param param) {
        String path = param.getPath();
        From join = findOrCreateJoin(path);
        if (param instanceof FetchParam) {
            BiConsumer<CriteriaBuilder, FetchParent> criterion = ((FetchParam) param).getCriterion();
            criterion.accept(cb, join);
        } else if (param instanceof InRefsFetchParam) {
            InRefsFetchParam irfp = (InRefsFetchParam) param;
            List<Object> collect = irfp.getIds().stream().map(id->session.load(irfp.getEntityClass(), id))
                    .collect(Collectors.toList());
            criteria.add(Restrictions.in(irfp.getPropertyName(), collect));
        }else if (param instanceof OrderParam) {


            join.addOrder(((OrderParam) param).getOrder());
        } else if (param instanceof PageParam) {
            criteria.setFirstResult(((PageParam) param).getFrom());
            criteria.setMaxResults(((PageParam) param).getCount());
        } else if (param instanceof SubqueryExistParam) {
            SubqueryExistParam subPar = (SubqueryExistParam) param;
            DetachedCriteria rootSubquery = DetachedCriteria.forClass(subPar.getSubqueryClass(), subPar.getAlias());
            for (FetchParam subParam : subPar.getSubqueryParams()) {
                createSubcriteriaJoin(subParam.getPath(), rootSubquery).add(subParam.getCriterion());
            }
            rootSubquery.add(Restrictions.eqProperty(subPar.getRootReference(), "root.id"));
            rootSubquery.setProjection(Projections.id());
            getRootCriteria().getCriteria().add(Subqueries.exists(rootSubquery));
        } else if (param instanceof AliasParam) {
            Validate.isInstanceOf(Subcriteria.class, criteria, "AliasParam path should be subcriteria");
            ((Subcriteria)criteria).setAlias(((AliasParam) param).getAlias());

        } else if (param instanceof CacheModeParam) {
            CacheModeParam cacheModeParam = CacheModeParam.class.cast(param);
            criteria.setCacheMode(cacheModeParam.getCacheMode());
        }
    }

    private DetachedCriteria createSubcriteriaJoin(String aPath, DetachedCriteria aDc) {
        String[] split = aPath.split("\\.");
        DetachedCriteria currentCriteria = aDc;
        for (String associationPath : split) {
            currentCriteria = currentCriteria.createCriteria(associationPath);
        }
        return currentCriteria;
    }*/

    private PropertyMeta addFilterJoins(String path) {
        List<String> addJoinsPath = new ArrayList<>();
        String[] split = path.split("\\.");
        String parentPath = null;
        for (String part : split) {
            parentPath = Joiner.on(".").skipNulls().join(parentPath, part);
            if (!criterias.containsKey(parentPath)) {
                addJoinsPath.add(parentPath);
            }
        }
        PropertyMeta lastAdded = null;
        for (String fieldPath : addJoinsPath) {
            Prop prop = DtoUtils.findProp(rootEntity, fieldPath);
            Validate.notNull(prop, "not found property %s in %s", fieldPath, rootEntity);
            if (IdentifiedEntity.class.isAssignableFrom(prop.getPropertyType())
                    || Collection.class.isAssignableFrom(prop.getPropertyType())) {
                From fetch = (From) root.fetch("fieldPath");
                PropertyMeta pc = new PropertyMeta(fetch, fieldPath, null);
                criterias.put(fieldPath, pc);
                lastAdded = pc;
            }
        }
        if (lastAdded != null) {
            return lastAdded;
        }
        throw new IllegalStateException("Not added any join");
    }

    private From findJoin(String aPath) {
        PropertyMeta propertyMeta = criterias.getOrDefault(aPath, null);
        return Optional.ofNullable(propertyMeta).map(p->p.from).orElseGet(null);
    }

    public static enum OrderType {
        ASC, DESC
    }

}