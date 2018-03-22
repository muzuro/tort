package com.mzr.tort.core.dao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;

import com.mzr.tort.core.extractor.DtoExtractor;
import com.mzr.tort.testsample.TestApplication;
import com.mzr.tort.testsample.TransactionHelperBean;
import com.mzr.tort.testsample.domain.dto.UniversityDto;
import com.mzr.tort.testsample.domain.entity.Form;
import com.mzr.tort.testsample.domain.entity.Student;
import com.mzr.tort.testsample.domain.entity.University;
import org.codehaus.janino.Java;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestApplication.class)
@DataJpaTest()
@EnableAutoConfiguration
@ComponentScan(basePackages = {"com.mzr.tort"})
public class ExtractorTest {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private DtoExtractor dtoExtractor;

    @Autowired
    private SimpleDao simpleDao;

    @Autowired
    private TransactionHelperBean transactionHelperBean;

    @Test
    public void fetchCollectionAllColumns() throws Exception {
        transactionHelperBean.doInTransaction(() -> {
            University kgtu = new University();
            kgtu.setName("kgtu");

            Form form = new Form();
            form.setName("form");
            form.setUniversity(kgtu);
            kgtu.getForms().add(form);

            Student misha = new Student("Misha");
            misha.setForm(form);
            form.getStudents().add(misha);
            entityManager.persist(kgtu);
        });

        List<University> holderList = new ArrayList<>();
        transactionHelperBean.doInTransaction(()-> {
            List<University> universities = dtoExtractor.fetch(UniversityDto.class, University.class,
                    Collections.emptyList());
            Assert.assertEquals(1, universities.size());
            holderList.add(universities.iterator().next());
            Set<Form> forms = holderList.iterator().next().getForms();
            Set<Student> students = forms.iterator().next().getStudents();
            Long studentId = students.iterator().next().getId();
        });

        //assert can get student id outside of transaction
        Long studentId = holderList.iterator().next().getForms().iterator().next().getStudents().iterator().next().getId();
        Assert.assertNotNull(studentId);
    }

}