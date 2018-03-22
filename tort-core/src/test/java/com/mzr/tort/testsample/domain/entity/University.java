package com.mzr.tort.testsample.domain.entity;

import com.mzr.tort.core.domain.LongIdEntity;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
public class University extends LongIdEntity {

    private String name;
    private Set<Form> forms = new HashSet<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "university", cascade = CascadeType.ALL)
    public Set<Form> getForms() {
        return forms;
    }

    public void setForms(Set<Form> forms) {
        this.forms = forms;
    }
}
