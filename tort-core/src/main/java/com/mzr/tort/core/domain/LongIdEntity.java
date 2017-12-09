package com.mzr.tort.core.domain;

import javax.persistence.*;
import java.util.Objects;

@MappedSuperclass
@Access(AccessType.PROPERTY)
public abstract class LongIdEntity implements IdentifiedEntity {

    private Long id;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }

    /**
     * @param id
     */
    protected void setId(Long id) {
        this.id = id;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : System.identityHashCode(this);
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof LongIdEntity) && (managedEquals(obj) || objectEquals(obj));
    }
    
    private boolean managedEquals(Object obj) {
        return (getId() != null) && Objects.equals(getId(), ((LongIdEntity) obj).getId());
    }
    
    private boolean objectEquals(Object obj) {
        return (getId() == null) && super.equals(obj);
    }
}