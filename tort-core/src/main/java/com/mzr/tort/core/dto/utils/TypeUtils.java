package com.mzr.tort.core.dto.utils;

import org.apache.commons.lang3.Validate;

import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Map;

public final class TypeUtils extends org.apache.commons.lang3.reflect.TypeUtils {

    private TypeUtils() {
    }

    public static <T extends Type> T getTypeArgument(Type aType, Class<?> aGeneric, String typeArgName) {
        Map<TypeVariable<?>, Type> map = getTypeArguments(aType, aGeneric);
        for (Map.Entry<TypeVariable<?>, Type> entry : map.entrySet()) {
            if (aGeneric.equals(entry.getKey().getGenericDeclaration()) && entry.getKey().getName().equals(typeArgName)) {
                return (T) entry.getValue();
            }
        }
        return null;
    }

    public static <T extends Type> T getTypeArgument(Type aType, Class<?> aGeneric) {
        Map<TypeVariable<?>, Type> map = getTypeArguments(aType, aGeneric);
        for (Map.Entry<TypeVariable<?>, Type> entry : map.entrySet()) {
            if (aGeneric.equals(entry.getKey().getGenericDeclaration())) {
                // noinspection unchecked
                return (T) entry.getValue();
            }
        }
        return null;
    }

    public static Class<?> getClassArgument(Prop fieldDef, Class<?> aGeneric) {
        Type typeArgument = getTypeArgument(fieldDef.getReadMethod().getGenericReturnType(), aGeneric);
        if (typeArgument instanceof Class) {
            return (Class<?>) typeArgument;
        } else if (typeArgument instanceof TypeVariable) {
            TypeVariable typeVariable = (TypeVariable) typeArgument;
            return (Class<?>) Validate.notNull(getTypeArguments(fieldDef.getOwnerClass(),
                    (Class<?>) typeVariable.getGenericDeclaration()).get(typeVariable));
        } else {
            throw new IllegalArgumentException(String.format("Невозможно определить дженерик '%s' для '%s'", aGeneric,
                    fieldDef));
        }
    }

}
