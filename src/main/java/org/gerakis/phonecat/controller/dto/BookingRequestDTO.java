package org.gerakis.phonecat.controller.dto;

import com.google.gson.annotations.SerializedName;

public record BookingRequestDTO(
        @SerializedName("phone_id") Long phoneId,
        String username
) {
}
