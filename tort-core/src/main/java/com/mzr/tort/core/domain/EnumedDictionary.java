package com.mzr.tort.core.domain;

/**
 */
public interface EnumedDictionary {

    String getName();

    @Deprecated
    default String getCaption() {
        return getName();
    }

}
