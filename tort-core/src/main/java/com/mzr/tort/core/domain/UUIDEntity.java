package com.mzr.tort.core.domain;

import org.hibernate.annotations.Type;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import java.util.UUID;

/**
 * @date 10.04.14
 */
@MappedSuperclass
@Access(AccessType.FIELD)
public abstract class UUIDEntity implements IdentifiedEntity {

    @Id
    @Type(type = "guid")
    private UUID id;

    /**
     * ctor
     */
    protected UUIDEntity() {
        this.id = UUID.randomUUID();
    }

    /**
     * ctor
     *
     * @param id
     */
    protected UUIDEntity(UUID id) {
        this.id = id;
    }

    public UUID getId() {
        return id;
    }

    protected void setId(UUID id) {
        this.id = id;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof UUIDEntity) && getId().equals(((UUIDEntity) obj).getId());
    }
}
