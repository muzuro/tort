package com.mzr.tort.core.dto;

/**
 *
 * @date: 15.05.15 14:16
 */
public class NamedDto implements IdentifiedDto {

    private String name;

    public String getId() {
        return name;
    }

    public String getName() {
        return name;
    }

    public void setName(String aName) {
        name = aName;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        NamedDto casted = (NamedDto) obj;

        if (name == null || casted.getId() == null) {
            return this == obj;
        }

        return (obj instanceof NamedDto) && name.equals(casted.getId());
    }

}