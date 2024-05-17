package org.gerakis.phonecat.service.dto;

import com.google.gson.annotations.SerializedName;
import java.time.LocalDateTime;

public record BookingConfirmationDTO (
    @SerializedName("phone_id") Long phoneId,
    @SerializedName("borrower_username") String borrowerUsername,
    @SerializedName("borrow_date") LocalDateTime borrowDate,
    boolean confirmed,
    String note
) {}
