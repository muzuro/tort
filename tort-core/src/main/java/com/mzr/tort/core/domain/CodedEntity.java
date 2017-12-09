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
public abstract class CodedEntity implements IdentifiedEntity {

    @Id
    @Column
    private String code;

    @Transient
    public String getId() {
        return code;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public int hashCode() {
        return code != null ? code.hashCode() : System.identityHashCode(this);
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof CodedEntity) && (getId() != null) && getId().equals(((CodedEntity) obj).getId());
    }

}