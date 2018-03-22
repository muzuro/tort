package com.mzr.tort.testsample.domain.dto;

import java.util.HashSet;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;

import com.mzr.tort.core.domain.LongIdEntity;
import com.mzr.tort.core.dto.LongIdDto;

public class UniversityDto extends LongIdDto {

    private String name;
    private Set<FormDto> forms = new HashSet<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<FormDto> getForms() {
        return forms;
    }

    public void setForms(Set<FormDto> forms) {
        this.forms = forms;
    }
}
