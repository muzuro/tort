package com.mzr.tort.sample.domain.entity

import java.util.HashSet

import com.mzr.tort.core.domain.LongIdEntity
import javax.persistence.CascadeType
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.OneToMany

@Entity
class University : LongIdEntity() {

    var name: String? = null
    @get:OneToMany(fetch = FetchType.LAZY, mappedBy = "university", cascade = [CascadeType.ALL])
    var forms: Set<Form> = HashSet()
}
