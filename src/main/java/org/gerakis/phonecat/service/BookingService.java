package org.gerakis.phonecat.service;

import org.gerakis.phonecat.service.dto.BookingInformationDTO;
import org.gerakis.phonecat.service.dto.PhoneDTO;
import org.springframework.stereotype.Service;

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
            PhoneDTO phone = databaseService.getPhone(phoneId);
            boolean confirmed;
            String message;
            if(phone == null) {
                return null;
            }
            if(phone.isAvailable()) {
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

        public BookingInformationDTO returnPhone(Long phoneId) {
            PhoneDTO phone = databaseService.getPhone(phoneId);
            if(phone != null) {
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
