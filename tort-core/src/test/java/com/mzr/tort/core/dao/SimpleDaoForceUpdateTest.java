package com.mzr.tort.core.dao;

import com.mzr.tort.testsample.TestApplication;
import com.mzr.tort.testsample.TransactionHelperBean;
import com.mzr.tort.testsample.domain.entity.Student;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTestContextBootstrapper;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.BootstrapWith;
import org.springframework.test.context.junit4.SpringRunner;

import javax.persistence.EntityManager;
import java.util.concurrent.atomic.AtomicLong;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestApplication.class)
@BootstrapWith(SpringBootTestContextBootstrapper.class)
@AutoConfigureDataJpa
@AutoConfigureTestDatabase
@AutoConfigureTestEntityManager
@EnableAutoConfiguration
@ComponentScan(basePackages = {"com.mzr.tort"})
public class SimpleDaoForceUpdateTest {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private SimpleDao simpleDao;

    @Autowired
    private TransactionHelperBean transactionHelperBean;

    @Test
    public void forceUpdate() throws Exception {
        AtomicLong id = new AtomicLong();
        AtomicLong oldVersion = new AtomicLong();
        transactionHelperBean.doInTransaction(() -> {
            Student student = new Student("John Doe");
            entityManager.persist(student);
            id.set(student.getId());
            oldVersion.set(student.getVersion());
        });

        transactionHelperBean.doInTransaction(() -> {
            Student student = entityManager.find(Student.class, id.get());
            simpleDao.forceUpdate(student);
            Assert.assertNotEquals(new Long(oldVersion.get()), student.getVersion());
        });

        transactionHelperBean.doInTransaction(() -> {
            Student student = entityManager.find(Student.class, id.get());
            //            student.setName("Jonathan Hype");
            Assert.assertNotEquals(new Long(oldVersion.get()), student.getVersion());
        });

    }

}