package org.gerakis.phonecat.service;

import jakarta.persistence.EntityManager;
import org.gerakis.phonecat.data.PhoneCatRepository;
import org.gerakis.phonecat.service.dto.BookingInformationDTO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;

@ExtendWith(MockitoExtension.class)
@SpringBootTest(classes=org.gerakis.phonecat.PhoneCatApplication.class,
        properties = {"spring.datasource.url=jdbc:h2:mem:phonecat_test_db", "spring.liquibase.contexts=test"} )
public class BookingServiceTest {

    @Autowired
    EntityManager entityManager;

    PhoneCatRepository repository;
    BookingService bookingService;

    String username = "gerakis";
    Long phoneId;

    @BeforeEach
    @Transactional
    public void setup() {
        this.repository = new PhoneCatRepository();
        ReflectionTestUtils.setField(repository, "entityManager", entityManager);
        phoneId = this.repository.addPhone("Apple", "iPhone 14", null);
        bookingService = new BookingService(repository);
    }

    @Test
    @Transactional
    public void bookingTest() {
        BookingInformationDTO bookInfo = bookingService.bookPhone(username, phoneId);
        Assertions.assertNotNull(bookInfo);
        Assertions.assertTrue(bookInfo.confirmed());
        Assertions.assertEquals(phoneId, bookInfo.phoneId());
        Assertions.assertEquals(username, bookInfo.borrowerUsername());
        Assertions.assertNotNull(bookInfo.borrowDate());
        Assertions.assertEquals(BookingService.BOOKING_CONFIRMED, bookInfo.note());
    }

    @Test
    @Transactional
    public void returnTest() {
        bookingService.bookPhone(username, phoneId);
        BookingInformationDTO bookInfo = bookingService.returnPhone(phoneId);
        Assertions.assertNotNull(bookInfo);
        Assertions.assertTrue(bookInfo.confirmed());
        Assertions.assertEquals(phoneId, bookInfo.phoneId());
        Assertions.assertEquals(username, bookInfo.borrowerUsername());
        Assertions.assertNotNull(bookInfo.borrowDate());
        Assertions.assertEquals(BookingService.RETURNED, bookInfo.note());
    }

    @Test
    @Transactional
    public void doubleBookingTest() {
        bookingService.bookPhone(username, phoneId);
        BookingInformationDTO bookInfo = bookingService.bookPhone(username, phoneId);
        Assertions.assertNotNull(bookInfo);
        Assertions.assertFalse(bookInfo.confirmed());
        Assertions.assertEquals(phoneId, bookInfo.phoneId());
        Assertions.assertEquals(username, bookInfo.borrowerUsername());
        Assertions.assertNotNull(bookInfo.borrowDate());
        Assertions.assertEquals(BookingService.UNAVAILABLE, bookInfo.note());
    }

    @Test
    @Transactional
    public void notFoundTest() {
        BookingInformationDTO bookInfo = bookingService.bookPhone(username, 74523L);
        Assertions.assertNotNull(bookInfo);
        Assertions.assertFalse(bookInfo.confirmed());
        Assertions.assertEquals(74523L, bookInfo.phoneId());
        Assertions.assertEquals(username, bookInfo.borrowerUsername());
        Assertions.assertNotNull(bookInfo.borrowDate());
        Assertions.assertEquals(BookingService.NOTFOUND, bookInfo.note());
    }
}
