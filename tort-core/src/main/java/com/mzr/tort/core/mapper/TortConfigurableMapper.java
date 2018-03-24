package com.mzr.tort.core.mapper;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import javax.annotation.PostConstruct;

import com.mzr.tort.core.ColorSchemeResolver;
import com.mzr.tort.core.domain.DetailedEnumedDictionary;
import com.mzr.tort.core.domain.EnumedDictionary;
import com.mzr.tort.core.domain.IdentifiedEntity;
import com.mzr.tort.core.dto.DetailedEnumedDictionaryDto;
import com.mzr.tort.core.dto.EnumedDictionaryDto;
import com.mzr.tort.core.dto.ExtendedEnumedDictionaryDto;
import com.mzr.tort.core.dto.IdentifiedDto;
import com.mzr.tort.core.dto.MappedDto;
import com.mzr.tort.core.dto.StyledEnumDictionaryDto;
import com.mzr.tort.core.dto.utils.DtoUtils;
import com.mzr.tort.core.dto.utils.Prop;
import com.mzr.tort.core.extractor.annotations.Mapped;
import com.mzr.tort.core.extractor.annotations.NotMapped;
import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.DefaultFieldMapper;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.i18n.LocaleContextHolder;

public class TortConfigurableMapper extends ConfigurableMapper {

    private static final LocalDate START_OF_UNIX_TIME_LOCAL_DATE = LocalDate.parse("1970-01-01");

    private final Logger logger = LoggerFactory.getLogger(TortConfigurableMapper.class);

    @Autowired
    private ApplicationContext applicationContext;

    private ColorSchemeResolver colorSchemeResolver;

    public TortConfigurableMapper() {
        super(false);
    }

    @PostConstruct
    public void init() {
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
                    public boolean canConvert(Type<?> aSourceType, Type<?> aDestinationType) {
                        Class<?> srcClass = aSourceType.getRawType();
                        Class<?> dstClass = aDestinationType.getRawType();
                        boolean aToB = EnumedDictionary.class.isAssignableFrom(srcClass) && EnumedDictionaryDto.class.isAssignableFrom(dstClass);
                        boolean bToA = EnumedDictionaryDto.class.isAssignableFrom(srcClass) && EnumedDictionary.class.isAssignableFrom(dstClass);
                        return aToB || bToA;
                    }

                    @Override
                    public EnumedDictionaryDto convertTo(EnumedDictionary aSource,
                            Type<EnumedDictionaryDto> aDestinationType, MappingContext mappingContext) {
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

                    /**
                     * @param aSource
                     * @return message from spring context, from class name or simple name
                     */
                    private String getEnumCaption(EnumedDictionary aSource) {
                        String simpleMessage = applicationContext.getMessage(String.format("%s.%s", aSource.getClass().getSimpleName(), aSource.getName()), null, LocaleContextHolder.getLocale());
                        String canonicalCode = String.format("%s.%s", aSource.getClass().getCanonicalName(), aSource.getName());
                        return applicationContext.getMessage(canonicalCode, null, simpleMessage, LocaleContextHolder.getLocale());
                    }

                    @Override
                    public EnumedDictionary convertFrom(EnumedDictionaryDto aSource, Type<EnumedDictionary> aDestinationType,
                            MappingContext mappingContext) {
                        Class rawType = aDestinationType.getRawType();
                        return (EnumedDictionary) Enum.valueOf(rawType, aSource.getName());
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
                LocalDateTime localDateTime = LocalDateTime.of(START_OF_UNIX_TIME_LOCAL_DATE, aSource);
                return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
            }
        });
        mapperFactory.getConverterFactory().registerConverter(new PassThroughConverter(LocalDate.class));
        mapperFactory.getConverterFactory().registerConverter(new PassThroughConverter(LocalTime.class));
//        mapperFactory.getConverterFactory().registerConverter(new CustomConverter<Duration, Duration>() {
//            @Override
//            public Duration convert(Duration aSource, Type<? extends Duration> aDestinationType) {
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

    /**
     * child can add custom mappers here
     * @param mapperFactory
     */
    protected void addClassMap(MapperFactory mapperFactory) {}

    /**
     * child can add custom enum mapping logic here, this method will be called for any enum convertion
     * @param aEnitity
     * @param aDto
     * @param aMapperFacade
     */
    protected void extendEnumMapping(EnumedDictionary aEnitity, EnumedDictionaryDto aDto, MapperFacade aMapperFacade) {}

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
