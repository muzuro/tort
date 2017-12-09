package com.mzr.tort.core.dto.utils;

import com.mzr.tort.core.extractor.annotations.NotMapped;
import org.apache.commons.lang3.Validate;
import org.springframework.beans.BeanUtils;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

public final class DtoUtils {

    private static final Map<Class<?>, PropertyDescriptorCache> PROPERTY_DESCRIPTOR_CACHE = new ConcurrentHashMap<>();

    private DtoUtils() {
    }

    public static <T> T writeProperty(String propertyName, Object bean, Object value) {
        return getProp(bean.getClass(), propertyName).writeProperty(bean,
                value);
    }

    public static <T> T readProperty(String propertyName, Object bean) {
        if (bean == null) {
            return null;
        }
        Prop propertyDescriptor;
        Class<?> propertyClass = bean.getClass();
        Object propertyBean = bean;
        for (String childProperty : propertyName.split("\\.")) {
            propertyDescriptor = DtoUtils.getProp(propertyClass,
                    childProperty);
            if (propertyBean == null) {
                return null;
            } else {
                propertyBean = propertyDescriptor.readProperty(propertyBean);
            }
            propertyClass = propertyDescriptor.getPropertyType();
        }
        return (T) propertyBean;
    }

    public static List<Prop> getProps(Class<?> dtoClass) {
        List<Prop> fieldDefs = new ArrayList<>();
        for (Prop fieldDef : getAllProps(dtoClass)) {
            Method reader = fieldDef.getReadMethod();
            if (reader != null && !Object.class.equals(reader.getDeclaringClass())) {
                fieldDefs.add(fieldDef);
            }
        }
        return fieldDefs;
    }

    public static List<Prop> getMappedProps(Class<?> dtoClass) {
        List<Prop> fieldDefs = new ArrayList<>();
        for (Prop fieldDef : getAllProps(dtoClass)) {
            Method reader = fieldDef.getReadMethod();
            if (reader != null && !Object.class.equals(reader.getDeclaringClass()) &&
                    !fieldDef.hasAnnotation(NotMapped.class)) {
                fieldDefs.add(fieldDef);
            }
        }
        return fieldDefs;
    }

    public static <T extends Annotation> T findAnnotation(
            Class<T> annotationClass, Class<?> dtoClass, String propertyName) {
        return getProp(dtoClass, propertyName).findAnnotation(
                annotationClass);
    }

    /**
     * @param aClass       класс откуда начинать искать пропертю
     * @param propertyName имя проперти, можно указать путь foo.boo.la
     * @return не синхронизованный кэшированный прокси для
     *         {@link java.beans.PropertyDescriptor} или null если не найден.
     */
    public static Prop findProp(Class<?> aClass, String propertyName) {
        Class<?> currentClass = aClass;
        Prop result = null;
        String[] split = propertyName.split("\\.");
        for (String propNamePart : split) {
            result = getPropertyDescriptorCache(Validate.notNull(currentClass)).getPropertyDescriptorMap().get(propNamePart);
            if (Objects.isNull(result)) {
                return result;
            }
            currentClass = getNoCollectionClass(result);
        }
        return result;
    }

    public static Class<?> getNoCollectionClass(Prop aProp) {
        if (Collection.class.isAssignableFrom(aProp.getPropertyType())) {
            return TypeUtils.getTypeArgument(aProp.getReadMethod().getGenericReturnType(),
                    Collection.class);
        } else {
            return aProp.getPropertyType();
        }
    }

    /**
     * @return не синхронизованный кэшированный прокси для
     *         {@link java.beans.PropertyDescriptor} .
     * @throws IllegalArgumentException если не найден.
     */
    public static Prop getProp(Class<?> aClass, String propertyName) {
        return Validate.notNull(findProp(aClass, propertyName),
                "Не найдено свойство %s в %s", propertyName, aClass);
    }

    /**
     * @return не синхронизованные кэшированные прокси для
     *         {@link java.beans.PropertyDescriptor}
     */
    public static List<Prop> getAllProps(Class<?> aClass) {
        return getPropertyDescriptorCache(aClass).getPropertyDescriptors();
    }

    public static Prop findProp(Class<?> aClass, List<String> propNameList) {
        Validate.notNull(aClass);
        Validate.notEmpty(propNameList);

        String propName = propNameList.get(0);
        Prop prop = findProp(aClass, propName);

        if (prop != null && propNameList.size() > 1) {
            prop = findProp(prop.getPropertyType(), propNameList.subList(1, propNameList.size()));
        }

        return prop;
    }

    private static PropertyDescriptorCache getPropertyDescriptorCache(
            Class<?> aClass) {
        PropertyDescriptorCache propertyDescriptorCache = PROPERTY_DESCRIPTOR_CACHE.get(aClass);
        if (propertyDescriptorCache == null) {
            PropertyDescriptor[] rawDescriptors = BeanUtils.getPropertyDescriptors(aClass);
            rawDescriptors = Arrays.stream(rawDescriptors).filter(d -> d.getReadMethod() != null)
                    .toArray(PropertyDescriptor[]::new);
            List<Prop> propertyDescriptors = new ArrayList<>(rawDescriptors.length);
            Map<String, Prop> propertyDescriptorMap = new HashMap<>(rawDescriptors.length);
            try {
                for (PropertyDescriptor rawDescriptor : rawDescriptors) {
                    Prop fieldDef = new Prop(rawDescriptor, aClass);
                    propertyDescriptors.add(fieldDef);
                    propertyDescriptorMap.put(fieldDef.getName(), fieldDef);
                }
            } catch (IntrospectionException e) {
                throw new IllegalStateException(e);
            }
            propertyDescriptorCache = new PropertyDescriptorCache(
                    Collections.unmodifiableList(propertyDescriptors),
                    Collections.unmodifiableMap(propertyDescriptorMap));
            PROPERTY_DESCRIPTOR_CACHE.put(aClass,
                    propertyDescriptorCache);
        }
        return propertyDescriptorCache;
    }

    public static Method getMostSpecificMethod(Method method, Class<?> targetClass) {
        Class<?>[] allInterfacesForClass = ClassUtils
                .getAllInterfacesForClass(targetClass);
        for (Class<?> interfaceClass : allInterfacesForClass) {
            Method specificMethod = ReflectionUtils.findMethod(interfaceClass,
                    method.getName(), method.getParameterTypes());
            if (specificMethod != null) {
                return specificMethod;
            }
        }
        return method;
    }

    public static <T> T orDefault(Supplier<T> resolver, T defaultValue) {
        Optional<T> result = resolve(resolver);
        if (result.isPresent()) {
            return result.get();
        }
        return defaultValue;
    }

    public static <T> Optional<T> resolve(Supplier<T> resolver) {
        try {
            T result = resolver.get();
            return Optional.ofNullable(result);
        }
        catch (NullPointerException e) {
            return Optional.empty();
        }
    }
}
