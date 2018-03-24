package com.mzr.tort.sample.initialData

import com.mzr.tort.core.dao.SimpleDao
import com.mzr.tort.sample.domain.entity.Form
import com.mzr.tort.sample.domain.entity.Student
import com.mzr.tort.sample.domain.entity.University
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import javax.annotation.PostConstruct

@Component
class DataInitiator {

  @Autowired
  lateinit var simpleDao: SimpleDao;

  @PostConstruct
  fun init() {
    for (i in 1..100) {
      val university = University()
      university.name = "university_$i"
      val form = Form()
      form.name = "form_u$i"
      form.university = university
      university.forms.add(form)
      val student = Student("student_u$i")
      student.form = form
      form.students.add(student)
      simpleDao.save(university)
    }
  }

}
