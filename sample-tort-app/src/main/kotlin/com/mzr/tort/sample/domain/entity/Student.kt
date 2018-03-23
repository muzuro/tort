package com.mzr.tort.sample.domain.entity

import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.ManyToOne
import javax.persistence.Version

import com.mzr.tort.core.domain.FinishableLongIdEntity

@Entity
class Student : FinishableLongIdEntity {

    var name: String? = null

    @get:ManyToOne(fetch = FetchType.LAZY)
    var form: Form? = null

    @get:Version
    var version: Long? = null

    constructor() {
    }

    constructor(name: String) {
        this.name = name
    }

}