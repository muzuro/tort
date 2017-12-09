package com.mzr.tort.core.dao;

import com.mzr.tort.testsample.TestApplication;
import com.mzr.tort.testsample.domain.Student;
import org.hibernate.proxy.HibernateProxy;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestApplication.class)
@DataJpaTest()
@EnableAutoConfiguration
@ComponentScan(basePackages = {"com.mzr.tort"})
public class SimpleDaoTest {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private SimpleDao simpleDao;

//    @Before
//    public void setUp() throws Exception {
//
//    }

    @Test
    public void fetchAll() throws Exception {
        Student john = new Student();
        john.setName("John Doe");
        entityManager.persist(john);

        Student jane = new Student();
        jane.setName("Jane Foo");
        entityManager.persist(jane);

        List<Student> students = simpleDao.fetchAll(Student.class);
        Assert.assertTrue(students.size() == 2);
    }

    @Test
    public void findById() throws Exception {
        Student samanthaFox = new Student("Samantha Fox");
        entityManager.persist(samanthaFox);
        Student loadedSamanthaFox = simpleDao.findById(Student.class, samanthaFox.getId());
        Assert.assertNotNull(loadedSamanthaFox);
        Assert.assertEquals(loadedSamanthaFox, samanthaFox);
    }

    @Test
    public void getEntities() throws Exception {
        Student johnDoe = new Student("John Doe");
        entityManager.persist(johnDoe);
        Student janeFoo = new Student("Jane Foo");
        entityManager.persist(janeFoo);
        List<Student> students = simpleDao.getEntities(Student.class, Arrays.asList(johnDoe.getId(), janeFoo.getId()));
        Assert.assertTrue(students.contains(johnDoe));
        Assert.assertTrue(students.contains(janeFoo));
    }

    @Test
    public void loadById() throws Exception {
        Student johnDoe = new Student("John Doe");
        entityManager.persist(johnDoe);
        entityManager.detach(johnDoe);
        Student student = simpleDao.loadById(Student.class, johnDoe.getId());
        Assert.assertNotNull(student);
        Assert.assertTrue(student instanceof HibernateProxy);
    }

    @Test
    public void findByField() throws Exception {
        Student johnDoe = new Student("John Doe");
        entityManager.persist(johnDoe);
        Student student = simpleDao.findByField(Student.class, "name", "John Doe");
        Assert.assertNotNull(student);
    }

    @Test
    public void fetchByField() throws Exception {
        Student johnDoe1 = new Student("John Doe");
        entityManager.persist(johnDoe1);
        Student johnDoe2 = new Student("John Doe");
        entityManager.persist(johnDoe2);
        List<Student> students = simpleDao.fetchByField(Student.class, "name", "John Doe");
        Assert.assertTrue(students.size() == 2);
        Assert.assertTrue(students.contains(johnDoe1));
        Assert.assertTrue(students.contains(johnDoe2));
    }

    @Test
    public void countByField() throws Exception {
        Student johnDoe1 = new Student("John Doe");
        entityManager.persist(johnDoe1);
        Student johnDoe2 = new Student("John Doe");
        entityManager.persist(johnDoe2);
        Student samJan = new Student("Sam Jan");
        entityManager.persist(samJan);
        Long count = simpleDao.countByField(Student.class, "name", "John Doe");
        Assert.assertTrue(count == 2);
    }

    @Test
    public void save() throws Exception {
        Long id = (Long) simpleDao.save(new Student("John Doe"));
        Student student = entityManager.find(Student.class, id);
        Assert.assertNotNull(student);
    }

    @Test
    public void saveOrUpdate() throws Exception {
        Student student = new Student("John Doe");
        simpleDao.saveOrUpdate(student);
        Student loadedStudent = entityManager.find(Student.class, student.getId());
        Assert.assertNotNull(loadedStudent);
        entityManager.detach(loadedStudent);
        student.setName("Karl Marks");
        simpleDao.saveOrUpdate(student);

        //flush and detach to load new instance from db and check
        entityManager.flush();
        entityManager.detach(student);

        loadedStudent = entityManager.find(Student.class, student.getId());
        Assert.assertEquals(student.getName(), loadedStudent.getName());
    }

    @Test
    public void update() throws Exception {
        Student student = new Student("John Doe");
        entityManager.persist(student);

        student.setName("Karl Marks");
        simpleDao.update(student);

        //flush and detach to load new instance from db and check
        entityManager.flush();
        entityManager.detach(student);

        Student loadedStudent = entityManager.find(Student.class, student.getId());
        Assert.assertEquals(student.getName(), loadedStudent.getName());
    }

    @Test
    public void refresh() throws Exception {
        Student student = new Student("John Doe");
        entityManager.persist(student);

        entityManager
                .createNativeQuery(String.format("update student set name = 'Karl Marks' where id = '%s'", student.getId()))
                .executeUpdate();
        simpleDao.refresh(student);
        Assert.assertEquals(student.getName(), "Karl Marks");
    }

    @Test
    @Ignore
    public void forceUpdate() throws Exception {
        Student student = new Student("John Doe");
        entityManager.persist(student);

        Long oldVersion = student.getVersion();
        simpleDao.forceUpdate(student);
        Assert.assertNotEquals(oldVersion, student.getVersion());
    }

    @Test
    public void delete() throws Exception {
        Student student = new Student("John Doe");
        entityManager.persist(student);
        simpleDao.delete(student);
        Assert.assertNull(entityManager.find(Student.class, student.getId()));
    }

    @Test
    public void evict() throws Exception {
        Student student = new Student("John Doe");
        entityManager.persist(student);
        simpleDao.evict(student);
        Assert.assertFalse(entityManager.contains(student));
    }

    @Test
    public void flush() throws Exception {
        Student student = new Student("John Doe");
        entityManager.persist(student);
        student.setName("Another name");
        simpleDao.flush();
        entityManager.detach(student);
        Assert.assertEquals("Another name", entityManager.find(Student.class, student.getId()).getName());
    }

    @Test
    public void clearSession() throws Exception {
        Student student = new Student("John Doe");
        entityManager.persist(student);
        student.setName("Another name");
        simpleDao.clearSession();
        Assert.assertFalse(entityManager.contains(student));
        Assert.assertNotEquals("Another name", entityManager.find(Student.class, student.getId()).getName());
    }

    @Test
    public void sessionContains() throws Exception {
        Student student = new Student("John Doe");
        entityManager.persist(student);
        Assert.assertTrue(simpleDao.sessionContains(student));
    }

    @Test
    public void fetchAllNotFinished() throws Exception {
        Student student1 = new Student("John Doe");
        entityManager.persist(student1);
        Student student2 = new Student("John Doe");
        entityManager.persist(student2);
        Student student3 = new Student("John Doe");
        student3.setFinishTime(new Date());
        entityManager.persist(student2);
        List<Student> students = simpleDao.fetchAllNotFinished(Student.class);
        Assert.assertTrue(students.size() == 2);
    }

}