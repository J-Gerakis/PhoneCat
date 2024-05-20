package org.gerakis.phonecat.infrastructure;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "Spec_Ref")
@Getter
@Setter
public class SpecificationEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "spec_ref_id", nullable = false)
    Long specRefId;

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
