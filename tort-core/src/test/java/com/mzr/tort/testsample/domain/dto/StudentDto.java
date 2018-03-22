package com.mzr.tort.testsample.domain.dto;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.Version;

import com.mzr.tort.core.domain.FinishableLongIdEntity;

public class StudentDto extends FinishableLongIdEntity {

    private String name;

    private FormDto form;

    private Long version;


    public StudentDto(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public FormDto getForm() {
        return form;
    }

    public void setForm(FormDto form) {
        this.form = form;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

}