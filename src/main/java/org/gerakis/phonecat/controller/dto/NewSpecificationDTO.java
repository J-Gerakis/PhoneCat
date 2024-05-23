package org.gerakis.phonecat.controller.dto;

import com.google.gson.annotations.SerializedName;

import static org.gerakis.phonecat.util.FilterUtil.ALPHANUM_ONLY;

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
        brand = brand.strip().replaceAll(ALPHANUM_ONLY, "");
        model = model.strip().replaceAll(ALPHANUM_ONLY, "");
        technology = technology.strip().replaceAll(ALPHANUM_ONLY, "");
        bands2g = bands2g.strip().replaceAll(ALPHANUM_ONLY, "");
        bands3g = bands2g.strip().replaceAll(ALPHANUM_ONLY, "");
        bands4g = bands2g.strip().replaceAll(ALPHANUM_ONLY, "");
    }
}
