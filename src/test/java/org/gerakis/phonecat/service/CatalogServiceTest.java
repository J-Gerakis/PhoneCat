package org.gerakis.phonecat.service;


import jakarta.persistence.EntityManager;
import org.apache.logging.log4j.util.Strings;
import org.gerakis.phonecat.controller.dto.NewPhoneRequestDTO;
import org.gerakis.phonecat.controller.dto.NewSpecificationDTO;
import org.gerakis.phonecat.data.PhoneCatRepository;
import org.gerakis.phonecat.service.dto.FullPhoneRecordDTO;
import org.gerakis.phonecat.service.dto.PhoneDTO;
import org.gerakis.phonecat.service.dto.SpecificationDTO;
import org.gerakis.phonecat.util.FilterUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Transactional
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@SpringBootTest(classes=org.gerakis.phonecat.PhoneCatApplication.class,
        properties = {"spring.datasource.url=jdbc:h2:mem:phonecat_test_db",
                "spring.liquibase.contexts=test"} )
public class CatalogServiceTest {

    @Autowired
    EntityManager entityManager;

    PhoneCatRepository repository;
    CatalogMaintenanceService catalogService;
    ApiCallService mockApi;

    String brand = "Samsung";
    String model = "Z4";
    String tech = "Some tech";
    String g2 = "g2";
    String g3 = "g3";
    String g4 = "g4";
    NewSpecificationDTO newSpecDTO = new NewSpecificationDTO(brand, model, tech, g2,g3,g4);

    @BeforeEach
    public void setup() {
        this.repository = new PhoneCatRepository();
        ReflectionTestUtils.setField(repository, "entityManager", entityManager);
        mockApi = mock(ApiCallService.class);
        when(mockApi.getDeviceInfo(anyString(), anyString())).thenReturn(newSpecDTO);
        catalogService = new CatalogMaintenanceService(repository, mockApi);
        ReflectionTestUtils.setField(catalogService, "fonoActive", true);
    }

    @Test
    public void addNewPhoneApiProvidedSpecTest() {
        NewPhoneRequestDTO request = new NewPhoneRequestDTO(brand, model, null, null,null,null);
        Long phoneId = catalogService.addNewPhone(request);

        Optional<PhoneDTO> optPhone = repository.getPhone(phoneId);
        Assertions.assertTrue(optPhone.isPresent());
        PhoneDTO fullPhone = optPhone.get();

        Assertions.assertEquals(brand, fullPhone.brand());
        Assertions.assertEquals(model, fullPhone.model());
        Assertions.assertEquals(true, fullPhone.isAvailable());
        Assertions.assertEquals(Strings.EMPTY, fullPhone.borrowerUsername());
        Assertions.assertNull(fullPhone.borrowDate());
        Assertions.assertNotNull(fullPhone.specRef());
        SpecificationDTO spec = fullPhone.specRef();
        Assertions.assertEquals(tech, spec.technology());
        Assertions.assertEquals(g2, spec.bands2g());
        Assertions.assertEquals(g3, spec.bands3g());
        Assertions.assertEquals(g4, spec.bands4g());

    }

    @Test
    public void addNewPhoneUserProvidedSpecTest() {
        ReflectionTestUtils.setField(catalogService, "fonoActive", false);
        NewPhoneRequestDTO request = new NewPhoneRequestDTO(brand, model, tech, g2,g3,g4);
        Long phoneId = catalogService.addNewPhone(request);

        Optional<PhoneDTO> optPhone = repository.getPhone(phoneId);
        Assertions.assertTrue(optPhone.isPresent());
        PhoneDTO fullPhone = optPhone.get();

        Assertions.assertEquals(brand, fullPhone.brand());
        Assertions.assertEquals(model, fullPhone.model());
        Assertions.assertEquals(true, fullPhone.isAvailable());
        Assertions.assertEquals(Strings.EMPTY, fullPhone.borrowerUsername());
        Assertions.assertNull(fullPhone.borrowDate());
        Assertions.assertNotNull(fullPhone.specRef());
        SpecificationDTO spec = fullPhone.specRef();
        Assertions.assertEquals(tech, spec.technology());
        Assertions.assertEquals(g2, spec.bands2g());
        Assertions.assertEquals(g3, spec.bands3g());
        Assertions.assertEquals(g4, spec.bands4g());
    }

    @Test
    public void addNewPhoneExistingSpecTest() {
        ReflectionTestUtils.setField(catalogService, "fonoActive", false);
        repository.addSpecification(newSpecDTO);
        NewPhoneRequestDTO request = new NewPhoneRequestDTO(brand, model, null, null,null,null);
        Long phoneId = catalogService.addNewPhone(request);

        Optional<PhoneDTO> optPhone = repository.getPhone(phoneId);
        Assertions.assertTrue(optPhone.isPresent());
        PhoneDTO fullPhone = optPhone.get();

        Assertions.assertEquals(brand, fullPhone.brand());
        Assertions.assertEquals(model, fullPhone.model());
        Assertions.assertEquals(true, fullPhone.isAvailable());
        Assertions.assertEquals(Strings.EMPTY, fullPhone.borrowerUsername());
        Assertions.assertNull(fullPhone.borrowDate());
        Assertions.assertNotNull(fullPhone.specRef());
        SpecificationDTO spec = fullPhone.specRef();
        Assertions.assertEquals(tech, spec.technology());
        Assertions.assertEquals(g2, spec.bands2g());
        Assertions.assertEquals(g3, spec.bands3g());
        Assertions.assertEquals(g4, spec.bands4g());
    }

    @Test
    public void addNewSpecification() {
        NewPhoneRequestDTO request = new NewPhoneRequestDTO(brand, model, null, null,null,null);
        Long phoneId = catalogService.addNewPhone(request);
        catalogService.addOrUpdateNewSpecification(newSpecDTO);
        Optional<PhoneDTO> optPhone = repository.getPhone(phoneId);
        Assertions.assertTrue(optPhone.isPresent());
        PhoneDTO fullPhone = optPhone.get();

        Assertions.assertEquals(brand, fullPhone.brand());
        Assertions.assertEquals(model, fullPhone.model());
        Assertions.assertEquals(true, fullPhone.isAvailable());
        Assertions.assertEquals(Strings.EMPTY, fullPhone.borrowerUsername());
        Assertions.assertNull(fullPhone.borrowDate());
        Assertions.assertNotNull(fullPhone.specRef());
        SpecificationDTO spec = fullPhone.specRef();
        Assertions.assertEquals(tech, spec.technology());
        Assertions.assertEquals(g2, spec.bands2g());
        Assertions.assertEquals(g3, spec.bands3g());
        Assertions.assertEquals(g4, spec.bands4g());
    }

    @Test
    public void getListWithFilterTest() {
        repository.addPhone("Samsung", "Z3", null);
        repository.addPhone("Samsung", "Galaxy S8", null);
        repository.addPhone("Apple", "iPhone 14", null);
        Map<String, String> filterParams = new HashMap<>();
        filterParams.put(FilterUtil.BRAND, "Samsung");
        List<PhoneDTO> list = catalogService.getAllPhones(filterParams);

        Assertions.assertEquals(2, list.size());
        list.forEach(p -> Assertions.assertEquals("Samsung", p.brand()));
    }
}
