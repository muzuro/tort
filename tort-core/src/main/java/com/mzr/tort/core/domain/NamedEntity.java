package com.mzr.tort.core.domain;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

/**
 */
@MappedSuperclass
@Access(AccessType.FIELD)
public abstract class NamedEntity implements IdentifiedEntity, Named {

    @Id
    @Column
    private String name;

    @Transient
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
    public int hashCode() {
        return name != null ? name.hashCode() : System.identityHashCode(this);
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof NamedEntity) && (getId() != null) && getId().equals(((NamedEntity) obj).getId());
    }

}