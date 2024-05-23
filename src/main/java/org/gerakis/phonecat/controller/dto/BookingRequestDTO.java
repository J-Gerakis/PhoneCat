package org.gerakis.phonecat.controller.dto;

import com.google.gson.annotations.SerializedName;

public record BookingRequestDTO(
        @SerializedName("phone_id") Long phoneId,
        String username
) {
    public BookingRequestDTO {
        username = username.trim().replaceAll("[^a-zA-Z0-9]", "");
    }
}
