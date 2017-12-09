package com.mzr.tort.core.extractor;

import com.mzr.tort.core.ColorSchemeResolver;
import com.mzr.tort.core.domain.DetailedEnumedDictionary;
import com.mzr.tort.core.domain.EnumedDictionary;
import com.mzr.tort.core.domain.IdentifiedEntity;
import com.mzr.tort.core.dto.*;
import com.mzr.tort.core.dto.utils.DtoUtils;
import com.mzr.tort.core.dto.utils.Prop;
import com.mzr.tort.core.extractor.annotations.Mapped;
import com.mzr.tort.core.extractor.annotations.NotMapped;
import ma.glasnost.orika.*;
import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.converter.builtin.PassThroughConverter;
import ma.glasnost.orika.impl.ConfigurableMapper;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import ma.glasnost.orika.metadata.ClassMapBuilder;
import ma.glasnost.orika.metadata.ClassMapBuilderFactory;
import ma.glasnost.orika.metadata.Type;
import ma.glasnost.orika.property.PropertyResolverStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.i18n.LocaleContextHolder;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.*;

/**
 *
 */
public class TortConfigurableMapper extends ConfigurableMapper implements InitializingBean {

    private static final LocalDate START_OF_UNIX_TIME_LOCAL_DATE = LocalDate.parse("1970-01-01");

    private final Logger logger = LoggerFactory.getLogger(TortConfigurableMapper.class);

    @Autowired
    private ApplicationContext applicationContext;

    private ColorSchemeResolver colorSchemeResolver;

    /**
     * Для spring. Инициализация запустится после того, как будут найдены все требуемые зависимости.
     */
    protected TortConfigurableMapper() {
        super(false);
    }

    /**
     * ctor для использования в коде
     *
     * @param applicationContext
     */
    public TortConfigurableMapper(ApplicationContext applicationContext) {
        super(false);
        this.applicationContext = applicationContext;
        internalInit();
        super.init();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        internalInit();
        super.init();
    }

    private void internalInit() {
        try {
            colorSchemeResolver = applicationContext.getBean(ColorSchemeResolver.class);
        } catch (BeansException e) {
            logger.warn("Can't resolve ColorSchemeResolver");
        }
    }
    
    protected ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    @Override
    protected void configureFactoryBuilder(DefaultMapperFactory.Builder aFactoryBuilder) {
        aFactoryBuilder.classMapBuilderFactory(new ClassMapBuilderFactory() {
            @Override
            protected <A, B> ClassMapBuilder<A, B> newClassMapBuilder(Type<A> aType, Type<B> aBType,
                                                                      MapperFactory aMapperFactory, PropertyResolverStrategy aPropertyResolver,
                                                                      DefaultFieldMapper[] aDefaults) {
                return new DtoClassMapBuilder<>(aType, aBType, aMapperFactory, aPropertyResolver, aDefaults);
            }
        });
    }

    @Override
    protected void configure(MapperFactory mapperFactory) {
        mapperFactory.getConverterFactory().registerConverter(
                new BidirectionalConverter<EnumedDictionary, EnumedDictionaryDto>() {
                    @Override
                    public EnumedDictionaryDto convertTo(EnumedDictionary aSource, Type<EnumedDictionaryDto> aDestinationType, MappingContext mappingContext) {
                        EnumedDictionaryDto enumedDictionaryDto;
                        Class<EnumedDictionaryDto> destinationType = aDestinationType.getRawType();
                        if (aSource instanceof DetailedEnumedDictionary && DetailedEnumedDictionaryDto.class.equals(destinationType)) {
                            DetailedEnumedDictionaryDto detailedDto;
                            try {
                                detailedDto = (DetailedEnumedDictionaryDto) destinationType.newInstance();
                                detailedDto.getDetails().putAll(((DetailedEnumedDictionary) aSource).getDetails());
                                enumedDictionaryDto = detailedDto;
                            } catch (InstantiationException | IllegalAccessException e) {
                                throw new IllegalStateException(e.getMessage(), e);
                            }
                        } else {
                            try {
                                enumedDictionaryDto = destinationType.newInstance();
                            } catch (InstantiationException | IllegalAccessException e) {
                                throw new IllegalStateException(e.getMessage(), e);
                            }
                        }
                        enumedDictionaryDto.setName(aSource.getName());


                        enumedDictionaryDto.setCaption(
                                getEnumCaption(aSource)
                        );

                        if (enumedDictionaryDto instanceof ExtendedEnumedDictionaryDto) {
                            ExtendedEnumedDictionaryDto extendedEnumedDictionaryDto = ExtendedEnumedDictionaryDto.class.cast(enumedDictionaryDto);

                            String key = String.format("%s.%s.short", aSource.getClass().getSimpleName(), aSource.getName());
                            extendedEnumedDictionaryDto.setShortName(
                                    applicationContext.getMessage(key, null, enumedDictionaryDto.getCaption(), LocaleContextHolder.getLocale())
                            );
                        }


                        if (enumedDictionaryDto instanceof StyledEnumDictionaryDto) {
                            if (colorSchemeResolver == null) {
                                //workaround, tlcConfigurableMapper инициализируется позже???
                                internalInit();
                            }

                            if (colorSchemeResolver != null) {
                                StyledEnumDictionaryDto styledEnumDictionaryDto = StyledEnumDictionaryDto.class.cast(enumedDictionaryDto);
                                styledEnumDictionaryDto.setColor(colorSchemeResolver.getBackgroundColor(aSource));
                            }
                        }

                        extendEnumMapping(aSource, enumedDictionaryDto, mapperFacade);
                        return enumedDictionaryDto;
                    }

                    @Override
                    public EnumedDictionary convertFrom(EnumedDictionaryDto source, Type<EnumedDictionary> aDestinationType,
                            MappingContext mappingContext) {
                        Class rawType = aDestinationType.getRawType();
                        return (EnumedDictionary) Enum.valueOf(rawType, source.getName());
                    }

                    @Override
                    public boolean canConvert(Type<?> aSourceType, Type<?> aDestinationType) {
                        Class<?> srcClass = aSourceType.getRawType();
                        Class<?> dstClass = aDestinationType.getRawType();
                        boolean aToB = EnumedDictionary.class.isAssignableFrom(srcClass) && EnumedDictionaryDto.class.isAssignableFrom(dstClass);
                        boolean bToA = EnumedDictionaryDto.class.isAssignableFrom(srcClass) && EnumedDictionary.class.isAssignableFrom(dstClass);
                        return aToB || bToA;
                    }

                    /**
                     * @param aSource
                     * @return месседж по каноникал нейм, если нет по симпл класс нейм
                     */
                    private String getEnumCaption(EnumedDictionary aSource) {
                        String simpleMessage = applicationContext.getMessage(String.format("%s.%s", aSource.getClass().getSimpleName(), aSource.getName()), null, LocaleContextHolder.getLocale());
                        String canonicalCode = String.format("%s.%s", aSource.getClass().getCanonicalName(), aSource.getName());
                        return applicationContext.getMessage(canonicalCode, null, simpleMessage, LocaleContextHolder.getLocale());
                    }

                });
        mapperFactory.getConverterFactory().registerConverter(new CustomConverter<LocalDate, Date>() {
            @Override
            public Date convert(LocalDate source, Type<? extends Date> destinationType, MappingContext mappingContext) {
                return Date.from(source.atStartOfDay(ZoneId.systemDefault()).toInstant());
            }

        });
        mapperFactory.getConverterFactory().registerConverter(new CustomConverter<LocalTime, Date>() {
            @Override
            public Date convert(LocalTime aSource, Type<? extends Date> aDestinationType, MappingContext mappingContext) {
                Instant instant = START_OF_UNIX_TIME_LOCAL_DATE.atTime(aSource).atZone(ZoneId.systemDefault()).toInstant();
                return Date.from(instant);
            }
        });
        mapperFactory.getConverterFactory().registerConverter(new PassThroughConverter(LocalDate.class));
        mapperFactory.getConverterFactory().registerConverter(new PassThroughConverter(LocalTime.class));
//        mapperFactory.getConverterFactory().registerConverter(new CustomConverter<Duration, Duration>() {
//            @Override
//            public Duration convert(Duration aSource, Type<? extends Duration> aDestinationType, MappingContext mappingContext) {
//                return new Duration(aSource.getMillis());
//            }
//        });
        mapperFactory.getConverterFactory().registerConverter(new CustomConverter<String, UUID>() {
            @Override
            public UUID convert(String aSource, Type<? extends UUID> aDestinationType, MappingContext mappingContext) {
                return UUID.fromString(aSource);
            }
        });

        mapperFactory.classMap(EnumedDictionary.class, EnumedDictionaryDto.class)
                .byDefault()
                .exclude("caption")
                .register();

        addClassMap(mapperFactory);
    }

    protected void addClassMap(MapperFactory mapperFactory) {
        //todo: вынести в компоненты
    }

    protected void extendEnumMapping(EnumedDictionary aEnitity, EnumedDictionaryDto aDto, MapperFacade aMapperFacade) {
    }

    private class DtoClassMapBuilder<A, B> extends ClassMapBuilder<A, B> {

        private Set<String> excluded = new HashSet<>();

        protected DtoClassMapBuilder(Type<A> aType, Type<B> bType, MapperFactory mapperFactory,
                                     PropertyResolverStrategy propertyResolver, DefaultFieldMapper... defaults) {
            super(aType, bType, mapperFactory, propertyResolver, defaults);
            excludeNotMapped();
        }

        @Override
        public ClassMapBuilder<A, B> exclude(String aFieldName) {
            ClassMapBuilder<A, B> exclude = super.exclude(aFieldName);
            excluded.add(aFieldName);
            return exclude;
        }

        @Override
        public ClassMapBuilder<A, B> byDefault(DefaultFieldMapper... withDefaults) {
            super.byDefault(withDefaults);

            Class<?> dtoClass = getBType().getRawType();

            boolean isMappedTo = IdentifiedDto.class.isAssignableFrom(getBType().getRawType()) ||
                    MappedDto.class.isAssignableFrom(getBType().getRawType());
            boolean shouldCustomizeMapped = isMappedTo && IdentifiedEntity.class.isAssignableFrom(getAType().getRawType());
            if (!shouldCustomizeMapped) {
                return this;
            }

            for (Prop prop : DtoUtils.getAllProps(dtoClass)) {
                //map props with @Mapped
                if (prop != null && prop.hasAnnotation(Mapped.class) && !excluded.contains(prop.getMappedName())) {
                    fieldMap(prop.getMappedName(), prop.getName()).add();
                }
            }

            return this;
        }

        private void excludeNotMapped() {
            excludeNotMapped(getBType(), getAType());
            excludeNotMapped(getAType(), getBType());
        }

        private void excludeNotMapped(Type<?> firstType, Type<?> secondType) {
            for (Prop prop : DtoUtils.getAllProps(firstType.getRawType())) {
                if (prop != null) {
                    if (prop.hasAnnotation(NotMapped.class)) {
                        exclude(prop.getName());
                    }
                }
            }
        }
    }

}
