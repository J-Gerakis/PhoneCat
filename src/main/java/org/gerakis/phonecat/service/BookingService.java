package org.gerakis.phonecat.service;

import org.gerakis.phonecat.service.dto.BookingInformationDTO;
import org.gerakis.phonecat.service.dto.PhoneDTO;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class BookingService {

        private static final String BOOKING_CONFIRMED = "booking confirmed";
        private static final String UNAVAILABLE = "phone unavailable";
        private static final String RETURNED = "phone returned";

        public final DatabaseService databaseService;

        public BookingService(DatabaseService databaseService) {
            this.databaseService = databaseService;
        }

        public BookingInformationDTO bookPhone(String username, Long phoneId) {
            Optional<PhoneDTO> optPhone = databaseService.getPhone(phoneId);
            boolean confirmed;
            String message;
            if(optPhone.isEmpty()) {
                return null;
            } else {
                PhoneDTO phone = optPhone.get();
                if (phone.isAvailable()) {
                    phone = phone.updateAsBooked(username);
                    databaseService.updatePhone(phone);
                    confirmed = true;
                    message = BOOKING_CONFIRMED;
                } else {
                    confirmed = false;
                    message = UNAVAILABLE;
                }
                return new BookingInformationDTO(
                        phone.phoneId(),
                        phone.borrowerUsername(),
                        phone.borrowDate(),
                        confirmed,
                        message
                );
            }
        }

        public BookingInformationDTO returnPhone(Long phoneId) {
            Optional<PhoneDTO> optPhone = databaseService.getPhone(phoneId);
            if(optPhone.isPresent()) {
                PhoneDTO phone = optPhone.get();
                phone = phone.updateAsAvailable();
                databaseService.updatePhone(phone);
                return new BookingInformationDTO(phoneId,
                        phone.borrowerUsername(),
                        phone.borrowDate(),
                        true,
                        RETURNED);
            } else return null; //to be refined
        }

}
