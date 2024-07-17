package org.gerakis.phonecat.service.dto;

import com.google.gson.annotations.SerializedName;

public record SpecificationDTO(
        @SerializedName("spec_ref_id") Long specRefId,
        @SerializedName("brand") String brand,
        @SerializedName("model") String model,
        String technology,
        @SerializedName("_2g_bands") String bands2g,
        @SerializedName("_3g_bands") String bands3g,
        @SerializedName("_4g_bands") String bands4g
) {
    public static SpecificationDTO EmptySpecificationDTO() {
        return new SpecificationDTO(0L, null, null, null, null, null, null);

    }
}
