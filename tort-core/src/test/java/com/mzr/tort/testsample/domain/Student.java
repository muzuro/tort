package com.mzr.tort.testsample.domain;

import com.mzr.tort.core.domain.FinishableLongIdEntity;

import javax.persistence.Entity;
import javax.persistence.Version;

@Entity
public class Student extends FinishableLongIdEntity {

    private String name;

    private Long version;

    public Student() {
        System.out.println("some");
    }

    public Student(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Version
    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

}