package com.mzr.tort.core.extractor;

import com.mzr.tort.core.extractor.param.Param;

import com.mzr.tort.core.domain.IdentifiedEntity;
import com.mzr.tort.core.dto.IdentifiedDto;
import org.hibernate.Criteria;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaQuery;

/**
 *
 */
public class DtoExtractorImpl implements DtoExtractor {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public <D extends IdentifiedDto, E extends IdentifiedEntity> List<E> fetch(Class<D> aDtoClass, Class<E> aEntityClass, List<Param> aParams) {
        TortCriteriaBuilder tcb = new TortCriteriaBuilder(aEntityClass, aDtoClass, entityManager);
        CriteriaQuery criteriaQuery = tcb.buildCriteria();
        return entityManager.createQuery(criteriaQuery).getResultList();
    }

    //    @Override
//    public <D extends IdentifiedDto, E extends IdentifiedEntity> E find(Class<D> aDtoClass, Class<E> aEntityClass,
//            Serializable aId) {
//        return null;
//    }

    //    // @Autowired
//    private DtoDao dtoDao;
//
//    // @Resource(name = "tlcConfigurableMapper")
//    private TortConfigurableMapper mapper;
//
//    public DtoExtractorImpl(DtoDao aDtoDao, TortConfigurableMapper aMapper) {
//        dtoDao = aDtoDao;
//        mapper = aMapper;
//    }
//
//    // public void setSessionFactory(SessionFactory sessionFactory) {
//    // dtoDao.setSessionFactory(sessionFactory);
//    // }
//    //
//    // public final SessionFactory getSessionFactory() {
//    // return dtoDao.getSessionFactory();
//    // }
//
//    @Transactional(readOnly = true)
//    public <D extends IdentifiedDto, E extends IdentifiedEntity> E find(Class<D> aDtoClass, Class<E> aEntityClass,
//            Serializable aId) {
//        return dtoDao.find(aEntityClass, aDtoClass, aId);
//    }
//
//    @Transactional(readOnly = true)
//    public <D extends IdentifiedDto, E extends IdentifiedEntity> E find(Class<D> aDtoClass, Class<E> aEntityClass,
//            Param... aFetchParams) {
//        return dtoDao.findByFetch(aEntityClass, aDtoClass, aFetchParams);
//    }
//
//    @Transactional(readOnly = true)
//    public <E extends IdentifiedEntity> E find(Class<E> aEntityClass, Param... aFetchParams) {
//        return dtoDao.findByFetch(aEntityClass, aFetchParams);
//    }
//
//    @Transactional(readOnly = true)
//    public <E extends IdentifiedEntity> E find(Class<E> aEntityClass, List<Param> aParams) {
//        return find(aEntityClass, aParams.toArray(new Param[aParams.size()]));
//    }
//
//    @Transactional(readOnly = true)
//    public <D extends IdentifiedDto, E extends IdentifiedEntity> D findDto(Class<D> aDtoClass, Class<E> aEntityClass,
//            Serializable aId) {
//        E entity = dtoDao.find(aEntityClass, aDtoClass, aId);
//        return mapper.map(entity, aDtoClass);
//    }
//
//    @Transactional(readOnly = true)
//    public <E extends IdentifiedEntity> E find(Class<E> aEntityClass, Serializable aId) {
//        return dtoDao.find(aEntityClass, IdentifiedDto.class, aId);
//    }
//
//    @Transactional(readOnly = true)
//    public <D extends IdentifiedDto, E extends IdentifiedEntity> D findDto(Class<D> aDtoClass, Class<E> aEntityClass,
//            Param... aFetchParams) {
//        E entity = dtoDao.findByFetch(aEntityClass, aDtoClass, aFetchParams);
//        return mapper.map(entity, aDtoClass);
//    }
//
//    @Transactional(readOnly = true)
//    public <E extends IdentifiedEntity> List<E> fetch(Class<E> aEntityClass, Param... aParams) {
//        return dtoDao.fetch(aEntityClass, aParams);
//    }
//
//    @Transactional(readOnly = true)
//    public <E extends IdentifiedEntity> List<E> fetch(Class<E> aEntityClass, FlushMode aFlushMode, Param... aParams) {
//        return dtoDao.fetch(aEntityClass, aFlushMode, aParams);
//    }
//
//    @Transactional(readOnly = true)
//    public <E extends IdentifiedEntity> List<E> fetch(Class<E> aEntityClass, List<Param> aParams) {
//        return fetch(aEntityClass, aParams.toArray(new Param[aParams.size()]));
//    }
//
//    @Transactional(readOnly = true)
//    public <D extends IdentifiedDto, E extends IdentifiedEntity> List<E> fetch(Class<D> aDtoClass,
//            Class<E> aEntityClass, Param... aParams) {
//        return dtoDao.fetch(aEntityClass, aDtoClass, aParams);
//    }
//
//    @Transactional(readOnly = true)
//    public <D extends IdentifiedDto, E extends IdentifiedEntity> List<E> fetch(Class<D> aDtoClass,
//            Class<E> aEntityClass, List<Param> aParams) {
//        return fetch(aDtoClass, aEntityClass, aParams.toArray(new Param[aParams.size()]));
//    }
//
//    @Transactional(readOnly = true)
//    public <D extends IdentifiedDto, E extends IdentifiedEntity> List<E> fetch(Class<D> aDtoClass,
//            Class<E> aEntityClass, CacheMode aCacheMode, List<Param> aParams) {
//        return dtoDao.fetch(aEntityClass, aDtoClass, aCacheMode, aParams.toArray(new Param[aParams.size()]));
//    }
//
//    @Transactional(readOnly = true)
//    public <D extends IdentifiedDto, E extends IdentifiedEntity> List<D> fetchDto(Class<D> aDtoClass,
//            Class<E> aEntityClass, Param... aParams) {
//        List<E> entities = dtoDao.fetch(aEntityClass, aDtoClass, aParams);
//        return mapper.mapAsList(entities, aDtoClass);
//    }
//
//    @Transactional(readOnly = true)
//    public <D extends IdentifiedDto, E extends IdentifiedEntity> List<D> fetchDto(Class<D> aDtoClass,
//            Class<E> aEntityClass, CacheMode cacheMode, Param... aParams) {
//        List<E> entities = dtoDao.fetch(aEntityClass, aDtoClass, cacheMode, aParams);
//        return mapper.mapAsList(entities, aDtoClass);
//    }
//
//    @Transactional(readOnly = true)
//    public <D extends IdentifiedDto, E extends IdentifiedEntity> List<D> fetchDto(Class<D> aDtoClass,
//            Class<E> aEntityClass, List<Param> aParams) {
//        return fetchDto(aDtoClass, aEntityClass, aParams.toArray(new Param[aParams.size()]));
//    }
//
//    @Override
//    public <D extends IdentifiedDto, E extends IdentifiedEntity> List<D> fetchDto(Class<D> aDtoClass,
//            Class<E> aEntityClass, BiConsumer<D, E> aPostMapper, List<Param> aParams) {
//        List<E> entities = dtoDao.fetch(aEntityClass, aDtoClass, aParams.toArray(new Param[aParams.size()]));
//        List<D> result = new ArrayList<>();
//        for (E entity : entities) {
//            D dto = mapper.map(entity, aDtoClass);
//            aPostMapper.accept(dto, entity);
//            result.add(dto);
//        }
//        return result;
//    }
//
//    @Transactional(readOnly = true)
//    public <E extends IdentifiedEntity> long calculateCount(Class<E> aEntityClass, Param... aParams) {
//        return dtoDao.calculateCount(aEntityClass, aParams);
//    }
//
//    @Transactional(readOnly = true)
//    public <E extends IdentifiedEntity> long calculateCount(Class<E> aEntityClass, List<Param> aParams) {
//        return dtoDao.calculateCount(aEntityClass, aParams.toArray(new Param[aParams.size()]));
//    }
//
//    @Override
//    @Transactional(readOnly = true)
//    public <E extends IdentifiedEntity> long calculateDistinctCount(Class<E> aEntityClass, List<Param> aParams) {
//        return dtoDao.calculateDistinctCount(aEntityClass, aParams.toArray(new Param[aParams.size()]));
//    }
//
//    @Transactional(readOnly = true)
//    public <E extends IdentifiedEntity> List<Serializable> fetchIds(Class<E> aEntityClass, Param... aParams) {
//        return dtoDao.fetchIds(aEntityClass, Arrays.asList(aParams));
//    }
//
//    @Transactional(readOnly = true)
//    public <E extends IdentifiedEntity> List<Serializable> fetchIds(Class<E> aEntityClass, List<Param> aParams) {
//        return dtoDao.fetchIds(aEntityClass, aParams);
//    }
//
//    @Transactional(readOnly = true)
//    public <E extends IdentifiedEntity> List<Serializable> fetchDistinctIds(Class<E> aEntityClass,
//            List<Param> aParams) {
//        return dtoDao.fetchDistinctIds(aEntityClass, aParams);
//    }

}
