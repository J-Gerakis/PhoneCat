package org.gerakis.phonecat.service;


import jakarta.persistence.EntityManager;
import org.gerakis.phonecat.data.PhoneCatRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
@SpringBootTest(classes=org.gerakis.phonecat.PhoneCatApplication.class,
        properties = {"spring.datasource.url=jdbc:h2:mem:phonecat_test_db", "spring.liquibase.contexts=test"} )
public class CatalogServiceTest {

    @Autowired
    EntityManager entityManager;

    PhoneCatRepository repository;
    CatalogMaintenanceService service;

    @BeforeEach
    public void setup() {
        this.repository = new PhoneCatRepository();
        ReflectionTestUtils.setField(repository, "entityManager", entityManager);

        service = new CatalogMaintenanceService(repository, new ApiCallService());
    }


}
