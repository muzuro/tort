package com.mzr.tort.core.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.mzr.tort.core.domain.EnumedDictionary;

@JsonIgnoreProperties(ignoreUnknown = true)
public class EnumedDictionaryDto implements EnumedDictionary {

    private String name;
    private String caption;

    public String getName() {
        return name;
    }

    public EnumedDictionaryDto() {
    }

    public EnumedDictionaryDto(String name) {
        this.name = name;
    }

    /**
     * ctor
     *
     * @param name
     * @param caption
     */
    @Deprecated
    public EnumedDictionaryDto(String name, String caption) {
        this.name = name;
        this.caption = caption;
    }

    /**
     * ctor
     *
     * @param e Enum
     */
    public EnumedDictionaryDto(EnumedDictionary e) {
        this.name = e.getName();
    }

    /**
     * ctor
     *
     * @param e           Enum
     * @param displayName Display name
     */
    public EnumedDictionaryDto(EnumedDictionary e, String displayName) {
        this.name = e.getName();
        this.caption = displayName;
    }

    public void setName(String aName) {
        name = aName;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String aCaption) {
        caption = aCaption;
    }

    @Deprecated
    public static EnumedDictionaryDto valueOf(EnumedDictionary enumedDictionary) {
        if (enumedDictionary != null) {
            return new EnumedDictionaryDto(enumedDictionary.getName(), enumedDictionary.getCaption());
        } else {
            return null;
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        EnumedDictionaryDto casted = (EnumedDictionaryDto) obj;

        if (name == null || casted.getName() == null) {
            return this == obj;
        }

        return (obj instanceof EnumedDictionaryDto) && name.equals(casted.getName());
    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : super.hashCode();
    }

}
