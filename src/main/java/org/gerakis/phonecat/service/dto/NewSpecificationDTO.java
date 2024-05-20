package org.gerakis.phonecat.service.dto;

import com.google.gson.annotations.SerializedName;

public record NewSpecificationDTO(
        @SerializedName("brand_model") String brandModel,
        String technology,
        @SerializedName("_2g_bands") String bands2g,
        @SerializedName("_3g_bands") String bands3g,
        @SerializedName("_4g_bands") String bands4g
) {
}
