package org.gerakis.phonecat.controller.dto;

import com.google.gson.annotations.SerializedName;

public record MessageDTO(
    String message,
    @SerializedName("entity_id") Long entityId
) {}
