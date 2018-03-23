package com.mzr.tort.sample.controller

import com.mzr.tort.sample.domain.dto.UniversityDto
import com.mzr.tort.sample.domain.entity.University
import com.mzr.tort.sample.service.UniversityService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class UniversityController {

    @Autowired
    lateinit var universityService: UniversityService;

    @GetMapping("/hello")
    fun helloKotlin(): List<UniversityDto> {
        return universityService.fetch();
    }

}