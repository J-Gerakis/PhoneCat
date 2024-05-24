package org.gerakis.phonecat.data;

import jakarta.persistence.EntityManager;
import org.apache.logging.log4j.util.Strings;
import org.gerakis.phonecat.controller.dto.NewSpecificationDTO;
import org.gerakis.phonecat.service.dto.FullPhoneRecordDTO;
import org.gerakis.phonecat.service.dto.PhoneDTO;
import org.gerakis.phonecat.service.dto.SpecificationDTO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Transactional
@ExtendWith(MockitoExtension.class)
@SpringBootTest(classes=org.gerakis.phonecat.PhoneCatApplication.class,
        properties = {"spring.datasource.url=jdbc:h2:mem:phonecat_test_db", "spring.liquibase.contexts=test"} )
public class RepositoryTest {

    @Autowired
    EntityManager entityManager;

    PhoneCatRepository repository;

    //various data
    String brand = "Samsung";
    String model = "Z3";
    String username = "gerakis";
    LocalDateTime time = LocalDateTime.now();
    String tech = "Some tech";
    String g2 = "g2";
    String g3 = "g3";
    String g4 = "g4";
    NewSpecificationDTO newSpecDTO = new NewSpecificationDTO(brand, model, tech, g2,g3,g4);

    @BeforeEach
    public void setup() {
        this.repository = new PhoneCatRepository();
        ReflectionTestUtils.setField(repository, "entityManager", entityManager);
    }


    @Test
    public void testInsertAndRetrievePhoneSimple() {
        Long createdPhoneId = repository.addPhone(brand, model, null);

        Optional<PhoneDTO> optPhone = repository.getPhone(createdPhoneId);
        Assertions.assertTrue(optPhone.isPresent());

        PhoneDTO phone = optPhone.get();
        Assertions.assertEquals(brand, phone.brand());
        Assertions.assertEquals(model, phone.model());
        Assertions.assertEquals(true, phone.isAvailable());
        Assertions.assertEquals(Strings.EMPTY, phone.borrowerUsername());
        Assertions.assertNull(phone.borrowDate());
        Assertions.assertNull(phone.specRefId());
    }

    @Test
    public void testInsertAndRetrieveSpecification() {
        Long specId = repository.addSpecification(newSpecDTO);

        Optional<SpecificationDTO> optSpec = repository.findSpecification(brand, model);
        Assertions.assertTrue(optSpec.isPresent());

        SpecificationDTO spec = optSpec.get();
        Assertions.assertEquals(specId, spec.specRefId());
        Assertions.assertEquals(brand, spec.brand());
        Assertions.assertEquals(model, spec.model());
        Assertions.assertEquals(tech, spec.technology());
        Assertions.assertEquals(g2, spec.bands2g());
        Assertions.assertEquals(g3, spec.bands3g());
        Assertions.assertEquals(g4, spec.bands4g());
    }

    @Test
    public void insertAndRetrievePhoneFullTest() {
        Long specId = repository.addSpecification(newSpecDTO);
        Long phoneId = repository.addPhone(brand, model, specId);

        Optional<FullPhoneRecordDTO> optFullPhone = repository.getFullPhoneRecord(phoneId);
        Assertions.assertTrue(optFullPhone.isPresent());

        FullPhoneRecordDTO fullPhone = optFullPhone.get();
        Assertions.assertEquals(brand, fullPhone.brand());
        Assertions.assertEquals(model, fullPhone.model());
        Assertions.assertEquals(true, fullPhone.isAvailable());
        Assertions.assertEquals(Strings.EMPTY, fullPhone.borrowerUsername());
        Assertions.assertNull(fullPhone.borrowDate());
        Assertions.assertEquals(tech, fullPhone.technology());
        Assertions.assertEquals(g2, fullPhone.bands2g());
        Assertions.assertEquals(g3, fullPhone.bands3g());
        Assertions.assertEquals(g4, fullPhone.bands4g());
    }

    @Test
    public void updatePhoneTest() {
        Long createdPhoneId = repository.addPhone(brand, model, null);

        PhoneDTO modifiedPhone = new PhoneDTO(createdPhoneId, brand, model, false, username, time, null);
        repository.updatePhone(modifiedPhone);

        Optional<PhoneDTO> optPhone = repository.getPhone(createdPhoneId);
        Assertions.assertTrue(optPhone.isPresent());

        PhoneDTO phone = optPhone.get();
        Assertions.assertEquals(brand, phone.brand());
        Assertions.assertEquals(model, phone.model());
        Assertions.assertEquals(false, phone.isAvailable());
        Assertions.assertEquals(username, phone.borrowerUsername());
        Assertions.assertEquals(time, phone.borrowDate());
    }

    @Test
    public void updateSpecification() {
        String newtech = "New Tech";
        Long specId = repository.addSpecification(newSpecDTO);
        NewSpecificationDTO modifiedSpec = new NewSpecificationDTO(
                brand, model, newtech, g2, g3, g4
        );
        Long returnedSpecId = repository.findAndUpdateSpecification(modifiedSpec);
        Assertions.assertEquals(specId, returnedSpecId);
        SpecificationDTO spec = repository.findSpecification(brand, model).orElse(null);
        Assertions.assertNotNull(spec);
        Assertions.assertEquals(newtech, spec.technology());
    }

    @Test
    @Disabled
    public void connectPhoneToNewSpecTest() {
        Long phoneId = repository.addPhone(brand, model, null);
        Long specId = repository.addSpecification(newSpecDTO);
        repository.updatePhoneSpec(specId, brand, model);
        Optional<PhoneDTO> optPhone = repository.getPhone(phoneId);
        Assertions.assertTrue(optPhone.isPresent());

        PhoneDTO phone = optPhone.get();
        Assertions.assertEquals(specId, phone.specRefId());
    }

    @Test
    public void deletePhoneTest() {
        Long phId = repository.addPhone(brand, model, null);
        repository.deletePhone(phId);
        Optional<PhoneDTO> optPhone = repository.getPhone(phId);
        Assertions.assertTrue(optPhone.isEmpty());
    }


    @Test
    public void getListTest() {
        repository.addPhone(brand, model, null);
        repository.addPhone(brand, model, null);
        repository.addPhone(brand, model, null);
        List<FullPhoneRecordDTO> list = repository.getAllPhones();
        Assertions.assertEquals(3, list.size());
    }

}
