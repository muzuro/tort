package com.mzr.tort.sample.domain.dto

import com.mzr.tort.core.domain.LongIdEntity
import com.mzr.tort.sample.domain.entity.Student
import com.mzr.tort.sample.domain.entity.University
import java.util.*

class FormDto : LongIdEntity() {

    var name: String? = null

    var students: Set<StudentDto> = HashSet()
}
