package com.mzr.tort.sample.domain.entity

import java.util.HashSet

import com.mzr.tort.core.domain.LongIdEntity
import javax.persistence.*

@Entity
class Form : LongIdEntity() {

    var name: String? = null

    @get:ManyToOne(fetch = FetchType.LAZY)
    var university: University? = null

    @get:OneToMany(fetch = FetchType.LAZY, mappedBy = "form", cascade = [CascadeType.ALL])
    var students: MutableSet<Student> = HashSet()
}
