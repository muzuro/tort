package com.mzr.tort.core.dao;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import javax.persistence.EntityManager;

import com.mzr.tort.core.extractor.DtoExtractor;
import com.mzr.tort.core.extractor.TortCriteriaBuilder;
import com.mzr.tort.testsample.TestApplication;
import com.mzr.tort.testsample.TransactionHelperBean;
import com.mzr.tort.testsample.domain.dto.UniversityDto;
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
            List<University> universities = dtoExtractor.extract(UniversityDto.class, University.class).list();
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

    @Test
    public void testFilter() throws Exception {
        transactionHelperBean.doInTransaction(() -> {
            University kgtu = new University();
            kgtu.setName("kgtu");
            entityManager.persist(kgtu);

            University kfu = new University();
            kfu.setName("kfu");
            entityManager.persist(kfu);

            University kai = new University();
            kai.setName("kai");
            entityManager.persist(kai);
        });

        transactionHelperBean.doInTransaction(()-> {
            List<University> universities = dtoExtractor.extract(UniversityDto.class, University.class)
                    .filter((cb, from)-> from.get("name").in("kgtu", "kfu"))
                    .list();
            Assert.assertEquals(2, universities.size());
        });
    }

    @Test
    public void testPage() throws Exception {
        Consumer<String> creator = (n) -> {
            University kgtu = new University();
            kgtu.setName(n);
            entityManager.persist(kgtu);
        };

        transactionHelperBean.doInTransaction(() -> {
            IntStream.range(0, 99).forEach((i)->creator.accept(String.format("%02d", i)));
        });

        transactionHelperBean.doInTransaction(()-> {
            Set<String> possible = new HashSet<>();
            IntStream.range(20, 30).forEach((i) -> possible.add(String.valueOf(i)));
            List<University> universities = dtoExtractor.extract(UniversityDto.class, University.class)
                    .order("name", TortCriteriaBuilder.OrderType.ASC)
                    .list(20, 10);
            Assert.assertEquals(10, universities.size());
            Assert.assertTrue(universities.stream().allMatch((u) -> possible.contains(u.getName())));
        });
    }

    @Test
    public void testPageWithMapping() throws Exception {
        Consumer<String> creator = (n) -> {
            University kgtu = new University();
            kgtu.setName(n);
            entityManager.persist(kgtu);
        };

        transactionHelperBean.doInTransaction(() -> {
            IntStream.range(0, 99).forEach((i)->creator.accept(String.format("%02d", i)));
        });

        transactionHelperBean.doInTransaction(()-> {
            Set<String> possible = new HashSet<>();
            IntStream.range(20, 30).forEach((i) -> possible.add(String.valueOf(i)));
            List<UniversityDto> universities = dtoExtractor.extract(UniversityDto.class, University.class)
                    .order("name", TortCriteriaBuilder.OrderType.ASC)
                    .listDto(20, 10);
            Assert.assertEquals(10, universities.size());
            Assert.assertTrue(universities.stream().allMatch((u) -> possible.contains(u.getName())));
        });
    }

}
