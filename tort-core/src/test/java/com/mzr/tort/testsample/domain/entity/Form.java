package com.mzr.tort.testsample.domain.entity;

import com.mzr.tort.core.domain.LongIdEntity;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Form extends LongIdEntity {

    private String name;

    private University university;

    private Set<Student> students = new HashSet<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    public University getUniversity() {
        return university;
    }

    public void setUniversity(University university) {
        this.university = university;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "form", cascade = CascadeType.ALL)
    public Set<Student> getStudents() {
        return students;
    }

    public void setStudents(Set<Student> students) {
        this.students = students;
    }
}
