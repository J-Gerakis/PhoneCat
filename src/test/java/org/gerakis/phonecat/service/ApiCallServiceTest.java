package org.gerakis.phonecat.service;

import org.apache.logging.log4j.util.Strings;
import org.gerakis.phonecat.controller.dto.NewSpecificationDTO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@SpringBootTest(classes=org.gerakis.phonecat.PhoneCatApplication.class,
        properties = {"spring.datasource.url=jdbc:h2:mem:phonecat_test_db", "spring.liquibase.contexts=test"} )
public class ApiCallServiceTest {

    public static final String sampleReturnSrc = "./src/test/resources/fono_api_sample.json";

    String brand = "Samsung";
    String model = "Z4";
    String tech = "Some tech";
    String g2 = "g2";
    String g3 = "g3";
    String g4 = "g4";


    @Test
    public void callTest() throws IOException {
        String mockDataStr = Files.readString(Path.of(sampleReturnSrc));
        RestTemplate mockTemplate = mock(RestTemplate.class);
        when(mockTemplate.getForObject(anyString(), any(), anyMap())).thenReturn(mockDataStr);
        ApiCallService service = new ApiCallService();
        ReflectionTestUtils.setField(service, "template", mockTemplate);
        ReflectionTestUtils.setField(service, "getDeviceUrl", "https://blah");
        ReflectionTestUtils.setField(service, "token", "toktok");

        NewSpecificationDTO spec = service.getDeviceInfo(brand, model);
        Assertions.assertNotNull(spec);
        Assertions.assertEquals(tech, spec.technology());
        Assertions.assertEquals(g2, spec.bands2g());
        Assertions.assertEquals(g3, spec.bands3g());
        Assertions.assertEquals(g4, spec.bands4g());
    }

    @Test
    public void negativeTesting() {
        String mockDataStr = Strings.EMPTY;
        RestTemplate mockTemplate = mock(RestTemplate.class);
        when(mockTemplate.getForObject(anyString(), any(), anyMap())).thenReturn(mockDataStr);
        ApiCallService service = new ApiCallService();
        ReflectionTestUtils.setField(service, "template", mockTemplate);
        ReflectionTestUtils.setField(service, "getDeviceUrl", "https://blah");
        ReflectionTestUtils.setField(service, "token", "toktok");

        NewSpecificationDTO spec = Assertions.assertDoesNotThrow( () ->
                service.getDeviceInfo(brand, model)
        );
        Assertions.assertNull(spec);
    }

}
