package org.gerakis.phonecat.controller.dto;

import com.google.gson.annotations.SerializedName;

public record NewPhoneRequestDTO(
        String brand,
        String model,
        String technology,
        @SerializedName("_2g_bands") String bands2g,
        @SerializedName("_3g_bands") String bands3g,
        @SerializedName("_4g_bands") String bands4g
) {
}
