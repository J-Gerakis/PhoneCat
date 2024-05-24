package org.gerakis.phonecat.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.Strings;
import org.gerakis.phonecat.data.PhoneCatRepository;
import org.gerakis.phonecat.service.dto.BookingInformationDTO;
import org.gerakis.phonecat.service.dto.PhoneDTO;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class BookingService {

    public final static Logger logger = LogManager.getLogger(BookingService.class);

    public static final String BOOKING_CONFIRMED = "booking confirmed";
    public static final String UNAVAILABLE = "phone unavailable";
    public static final String RETURNED = "phone returned";
    public static final String NOTFOUND = "phone not found";

    private final PhoneCatRepository phoneCatRepository;

    public BookingService(PhoneCatRepository databaseService) {
        this.phoneCatRepository = databaseService;
    }

    public BookingInformationDTO bookPhone(String username, Long phoneId) {
        Optional<PhoneDTO> optPhone = phoneCatRepository.getPhone(phoneId);
        boolean confirmed;
        String message;
        if(optPhone.isEmpty()) {
            logger.debug("phone not found: {}", phoneId);
            confirmed = false;
            message = NOTFOUND;
        } else {
            logger.debug("phone found: {}", phoneId);
            PhoneDTO phone = optPhone.get();
            if (phone.isAvailable()) {
                phone = phone.updateAsBooked(username);
                phoneCatRepository.updatePhone(phone);
                confirmed = true;
                message = BOOKING_CONFIRMED;
                logger.debug("booking confirmed: {}", phoneId);
            } else {
                confirmed = false;
                message = UNAVAILABLE;
            }

        }
        return new BookingInformationDTO(
                phoneId,
                username,
                LocalDateTime.now(),
                confirmed,
                message
        );
    }

    public BookingInformationDTO returnPhone(Long phoneId) {
        Optional<PhoneDTO> optPhone = phoneCatRepository.getPhone(phoneId);
        boolean confirmed;
        String message;
        String username;
        LocalDateTime bookingTime;
        if(optPhone.isPresent()) {
            PhoneDTO phone = optPhone.get();
            username = phone.borrowerUsername();
            bookingTime = phone.borrowDate();
            confirmed = true;
            message = RETURNED;
            phone = phone.updateAsAvailable();
            phoneCatRepository.updatePhone(phone);
            logger.debug("return updated: {}", phoneId);
        } else {
            username = Strings.EMPTY;
            bookingTime = null;
            confirmed = false;
            message = NOTFOUND;
        }
        return new BookingInformationDTO(phoneId,
                username,
                bookingTime,
                confirmed,
                message);
    }

}
