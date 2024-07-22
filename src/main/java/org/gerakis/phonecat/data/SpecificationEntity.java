package org.gerakis.phonecat.data;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

//@NamedNativeQuery(name = "Spec.search",
//        query = "SELECT * FROM Spec_Ref sp WHERE UPPER(sp.brand) = UPPER(:brand) AND UPPER(sp.model) = UPPER(:model)",
//        resultClass = SpecificationEntity.class)

@NamedQuery(name = "Spec.search",
        query = "select sp from SpecificationEntity sp where " +
        "upper(sp.brand) = upper(:brand) and " +
        "upper(sp.model) = upper(:model)")

@Entity
@Table(name = "Spec_Ref")
@Getter
@Setter
public class SpecificationEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "spec_ref_id", nullable = false)
    Long specRefId;

    @Column(name = "brand", nullable = false)
    String brand;

    @Column(name = "model", nullable = false)
    String model;

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
