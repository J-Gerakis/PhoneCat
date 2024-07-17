package org.gerakis.phonecat.data;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.gerakis.phonecat.service.dto.FullPhoneRecordDTO;

import java.time.LocalDateTime;

@SqlResultSetMapping(name = "Phone.fullRecord",
        classes = @ConstructorResult(targetClass = FullPhoneRecordDTO.class, columns = {
        @ColumnResult(name = "phone_id", type = Long.class),
        @ColumnResult(name = "brand", type = String.class),
        @ColumnResult(name = "model", type = String.class),
        @ColumnResult(name = "is_available", type = Boolean.class),
        @ColumnResult(name = "borrower_username", type = String.class),
        @ColumnResult(name = "borrow_date", type = LocalDateTime.class),
        @ColumnResult(name = "technology", type = String.class),
        @ColumnResult(name = "_2g_bands", type = String.class),
        @ColumnResult(name = "_3g_bands", type = String.class),
        @ColumnResult(name = "_4g_bands", type = String.class),
}))
@NamedNativeQuery(name = "Phone.findFullRecord", query = "SELECT ph.phone_id, ph.brand, ph.model, ph.is_available, ph.borrower_username, ph.borrow_date, sp.technology, sp._2g_bands, sp._3g_bands, sp._4g_bands " +
        "FROM Phone ph LEFT JOIN Spec_Ref sp ON ph.spec_ref_id = sp.spec_ref_id WHERE ph.phone_id = :id", resultSetMapping = "Phone.fullRecord")

@NamedNativeQuery(name = "Phone.search", query = "SELECT ph.phone_id, ph.brand, ph.model, ph.is_available, ph.borrower_username, ph.borrow_date, sp.technology, sp._2g_bands, sp._3g_bands, sp._4g_bands " +
        "FROM Phone ph LEFT JOIN Spec_Ref sp ON ph.spec_ref_id = sp.spec_ref_id", resultSetMapping = "Phone.fullRecord")

@NamedNativeQuery(name = "Phone.updateSpec", query = "UPDATE Phone ph SET ph.spec_ref_id = :spec_ref_id WHERE UPPER(brand) = UPPER(:brand) AND UPPER(model) = UPPER(:model)")
@NamedQuery(name = "Phone.getAll", query = "select p from PhoneEntity p")
@Entity
@Table(name = "Phone")
@Getter
@Setter
public class PhoneEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "phone_id", nullable = false)
    Long phoneId;

    @Column(name = "brand", nullable = false)
    String brand;

    @Column(name = "model")
    String model;

    @Column(name = "is_available")
    boolean isAvailable;

    @Column(name = "borrower_username")
    String borrowerUsername;

    @Column(name = "borrow_date")
    LocalDateTime borrowDate;

    @JoinColumn(name = "spec_ref_id")
    @ManyToOne(fetch = FetchType.EAGER)
    SpecificationEntity specRef;

    public PhoneEntity() {}
}
