package com.mzr.tort.sample.domain.dto

import com.mzr.tort.core.dto.FinishableLongIdDto

class StudentDto : FinishableLongIdDto {

    var name: String? = null

    var version: Long? = null

    constructor() {
    }

    constructor(name: String) {
        this.name = name
    }

}
