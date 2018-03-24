package com.mzr.tort.sample.service

import com.mzr.tort.core.extractor.DtoExtractor
import com.mzr.tort.sample.domain.dto.UniversityDto
import com.mzr.tort.sample.domain.entity.University
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class UniversityService {

    @Autowired
    lateinit var dtoExtractor: DtoExtractor;

    fun fetch(from: Int, count: Int): List<UniversityDto> {
        return dtoExtractor.extract(UniversityDto::class.java, University::class.java)
           .listDto(from, count);
    }

}
