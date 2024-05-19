package org.gerakis.phonecat.service.dto;

import com.google.gson.annotations.SerializedName;
import org.apache.logging.log4j.util.Strings;

import java.time.LocalDateTime;

public record PhoneDTO(
        @SerializedName("phone_id") Long phoneId,
        String brand,
        String model,
        @SerializedName("is_available") Boolean isAvailable,
        @SerializedName("borrower_username") String borrowerUsername,
        @SerializedName("borrow_date") LocalDateTime borrowDate
) {

    public PhoneDTO updateAsBooked(String borrowerUsername) {
        return new PhoneDTO(phoneId, brand, model, false, borrowerUsername, LocalDateTime.now());
    }

    public PhoneDTO updateAsAvailable() {
        return new PhoneDTO(phoneId, brand, model, true, Strings.EMPTY, null);
    }

}
