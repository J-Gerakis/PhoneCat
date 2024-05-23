package org.gerakis.phonecat.controller.dto;

import com.google.gson.annotations.SerializedName;

import static org.gerakis.phonecat.util.FilterUtil.ALPHANUM_ONLY;

public record BookingRequestDTO(
        @SerializedName("phone_id") Long phoneId,
        String username
) {
    public BookingRequestDTO {
        username = username.strip().replaceAll(ALPHANUM_ONLY, "");
    }
}
