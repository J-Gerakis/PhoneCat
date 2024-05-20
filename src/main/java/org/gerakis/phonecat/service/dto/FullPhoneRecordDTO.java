package org.gerakis.phonecat.service.dto;

import com.google.gson.annotations.SerializedName;

import java.time.LocalDateTime;

public record FullPhoneRecordDTO(
        @SerializedName("phone_id") Long phoneId,
        String brand,
        String model,
        @SerializedName("is_available") Boolean isAvailable,
        @SerializedName("borrower_username") String borrowerUsername,
        @SerializedName("borrow_date") LocalDateTime borrowDate,
        String technology,
        @SerializedName("_2g_bands") String bands2g,
        @SerializedName("_3g_bands") String bands3g,
        @SerializedName("_4g_bands") String bands4g
) { }
