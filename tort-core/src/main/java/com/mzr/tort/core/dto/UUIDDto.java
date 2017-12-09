package com.mzr.tort.core.dto;

import java.util.UUID;

public class UUIDDto implements IdentifiedDto {

    private UUID id;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        UUIDDto casted = (UUIDDto) obj;

        if (id == null || casted.getId() == null) {
            return this == obj;
        }

        return (obj instanceof UUIDDto) && id.equals(casted.getId());
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : super.hashCode();
    }
}
