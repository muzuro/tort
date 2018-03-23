package com.mzr.tort.core.dao;

import com.mzr.tort.testsample.TestApplication;
import com.mzr.tort.testsample.domain.entity.Form;
import com.mzr.tort.testsample.domain.entity.Student;
import com.mzr.tort.testsample.domain.entity.University;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.junit4.SpringRunner;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestApplication.class)
@DataJpaTest()
@EnableAutoConfiguration
@ComponentScan(basePackages = {"com.mzr.tort"})
public class ComplexQueryTest {

    @Autowired
    private EntityManager entityManager;

    @Test
    public void testComplexCriteria() throws Exception {
        University kgtu = new University();
        kgtu.setName("kgtu");

        Form some = new Form();
        some.setName("some");
        some.setUniversity(kgtu);
        kgtu.getForms().add(some);

        Student misha = new Student("Misha");
        misha.setForm(some);
        some.getStudents().add(misha);

        Student anna = new Student("Anna");
        anna.setForm(some);
        some.getStudents().add(anna);
        entityManager.persist(kgtu);

        {
            CriteriaBuilder cb = entityManager.getCriteriaBuilder();
            CriteriaQuery<University> criteria = cb.createQuery(University.class);
            Root<University> fromUniversity = criteria.from(University.class);
            Join<Object, Object> formJoin = fromUniversity.join("forms");
            Join<Object, Object> studentJoin = formJoin.join("students");
            criteria.where(cb.equal(studentJoin.get("name"), "Yosef"));
            TypedQuery<University> query = entityManager.createQuery(criteria);
            List<University> resultList = query.getResultList();
            Assert.assertTrue(resultList.isEmpty());
        }
        {
            CriteriaBuilder cb = entityManager.getCriteriaBuilder();
            CriteriaQuery<University> criteria = cb.createQuery(University.class);
            Root<University> fromUniversity = criteria.from(University.class);
            Join<Object, Object> formJoin = fromUniversity.join("forms");
            Join<Object, Object> studentJoin = formJoin.join("students");
            TypedQuery<University> query = entityManager.createQuery(criteria);
            criteria.where(cb.equal(studentJoin.get("name"), "Anna"));
            List<University> resultList = query.getResultList();
            System.out.println(resultList);
            Assert.assertFalse(resultList.isEmpty());
        }
    }

}