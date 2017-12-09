package com.mzr.tort.core.dto.utils;

import com.mzr.tort.core.extractor.annotations.Mapped;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.Validate;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ReflectionUtils;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.beans.PropertyEditor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class Prop extends PropertyDescriptor {
    private final PropertyDescriptor propertyDescriptor;
    private final Method readMethod;
    private final Method writeMethod;
    private final Class<?> propertyType;
    private Field propertyField;
    private final Map<Class<? extends Annotation>, Object> annotationMap = new ConcurrentHashMap<>();
    private final Class<?> ownerClass;

    Prop(PropertyDescriptor aWrappedPropertyDescriptor, Class<?> aOwnerClass)
            throws IntrospectionException {
        super(aWrappedPropertyDescriptor.getName(), aWrappedPropertyDescriptor
                .getReadMethod(), aWrappedPropertyDescriptor.getWriteMethod());
        propertyDescriptor = aWrappedPropertyDescriptor;
        readMethod = propertyDescriptor.getReadMethod();
        writeMethod = propertyDescriptor.getWriteMethod();
        propertyType = propertyDescriptor.getPropertyType();
        try {
            propertyField = aOwnerClass.getDeclaredField(getName());
        } catch (NoSuchFieldException | SecurityException e) {
            propertyField = ReflectionUtils.findField(aOwnerClass, getName());
        }
        ownerClass = aOwnerClass;
    }
    
    public String getMappedName() {
        Mapped mappedAnnotation = findAnnotation(Mapped.class);
        if (mappedAnnotation != null) {
            return mappedAnnotation.value();
        } 
        return super.getName();
    }
    
    public Class<?> getOwnerClass() {
        return ownerClass;
    }

    public <A extends Annotation> A findAnnotation(Class<A> annotationType) {
        Object annotation = annotationMap.get(annotationType);
        if (annotation == null) {
            if (propertyField != null) {
                annotation = propertyField.getAnnotation(annotationType);
            }
            if (annotation == null) {
                annotation = ObjectUtils.defaultIfNull(AnnotationUtils
                        .findAnnotation(getReadMethod(), annotationType),
                        ObjectUtils.NULL);
            }
            annotationMap.put(annotationType, annotation);
        }
        return ObjectUtils.NULL.equals(annotation) ? null : (A) annotation;
    }

    public boolean hasAnnotation(Class<? extends Annotation> annotationType) {
        return findAnnotation(annotationType) != null;
    }

    public boolean isPreferred() {
        return propertyDescriptor.isPreferred();
    }

    public Object getValue(String attributeName) {
        return propertyDescriptor.getValue(attributeName);
    }

    public Enumeration<String> attributeNames() {
        return propertyDescriptor.attributeNames();
    }

    public Field getPropertyField() {
        return propertyField;
    }

    public Class<?> getPropertyType() {
        return propertyType;
    }

    public Method getReadMethod() {
        return readMethod;
    }

    public Method getWriteMethod() {
        return writeMethod;
    }
    
    public boolean isCollection() {
        return Collection.class.isAssignableFrom(propertyType);
    }

    public boolean isBound() {
        return propertyDescriptor.isBound();
    }

    public boolean isConstrained() {
        return propertyDescriptor.isConstrained();
    }

    public Class<?> getPropertyEditorClass() {
        return propertyDescriptor.getPropertyEditorClass();
    }

    public PropertyEditor createPropertyEditor(Object bean) {
        return propertyDescriptor.createPropertyEditor(bean);
    }

    public <T> T readProperty(Object bean) {
        return (T) ReflectionUtils.invokeMethod(Validate.notNull(readMethod,
                "Property '%s' is not readable.", getName()), bean);
    }

    public <T> T writeProperty(Object bean, Object value) {
        return (T) ReflectionUtils.invokeMethod(Validate.notNull(writeMethod,
                "Property '%s' is not writable.", getName()), bean, value);
    }

}
