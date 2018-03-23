package com.mzr.tort.sample.domain.dto

import java.util.HashSet

import com.mzr.tort.core.dto.LongIdDto

class UniversityDto : LongIdDto() {

    var name: String? = null
    var forms: Set<FormDto> = HashSet()
}
