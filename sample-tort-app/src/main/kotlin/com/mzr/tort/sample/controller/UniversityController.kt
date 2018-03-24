package com.mzr.tort.sample.controller

import com.mzr.tort.sample.domain.dto.UniversityDto
import com.mzr.tort.sample.service.UniversityService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class UniversityController {

    @Autowired
    lateinit var universityService: UniversityService;

    @GetMapping("/api/universities")
    fun helloKotlin(@RequestParam from: Int, @RequestParam count: Int): List<UniversityDto> {
      val list = universityService.fetch(from, count)
      return list
    }

}
