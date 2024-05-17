package org.gerakis.phonecat.controller;

import com.google.gson.annotations.SerializedName;

public record BookingDTO(
        @SerializedName("phone_id") Long phoneId,
        String username
) {
}
