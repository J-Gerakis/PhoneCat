package org.gerakis.phonecat.infrastructure;


import com.google.gson.annotations.SerializedName;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "SpecRef")
@Getter
@Setter
public class SpecificationEntity {
    @Id
    @Column(name = "brand_model", nullable = false)
    String brandModel;

    @Column(name="technology")
    String technology;

    @Column(name="_2g_bands")
    String bands2g;

    @Column(name="_3g_bands")
    String bands3g;

    @Column(name="_4g_bands")
    String bands4g;

    public SpecificationEntity() {}
}
