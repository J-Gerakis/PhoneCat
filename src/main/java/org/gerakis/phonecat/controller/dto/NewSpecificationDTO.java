package org.gerakis.phonecat.controller.dto;

import com.google.gson.annotations.SerializedName;

public record NewSpecificationDTO(
        @SerializedName("brand") String brand,
        @SerializedName("model") String model,
        String technology,
        @SerializedName("_2g_bands") String bands2g,
        @SerializedName("_3g_bands") String bands3g,
        @SerializedName("_4g_bands") String bands4g
) {
    public NewSpecificationDTO {
        //sanitization
        brand = brand.trim().replaceAll("[^a-zA-Z0-9]", "");
        model = model.trim().replaceAll("[^a-zA-Z0-9]", "");
        technology = technology.trim().replaceAll("[^a-zA-Z0-9]", "");
        bands2g = bands2g.trim().replaceAll("[^a-zA-Z0-9]", "");
        bands3g = bands2g.trim().replaceAll("[^a-zA-Z0-9]", "");
        bands4g = bands2g.trim().replaceAll("[^a-zA-Z0-9]", "");
    }
}
