package com.mzr.tort.sample

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication
class SampleTortApplication

fun main(args: Array<String>) {
    SpringApplication.run(SampleTortApplication::class.java, *args)
}