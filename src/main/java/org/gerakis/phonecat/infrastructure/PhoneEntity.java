package org.gerakis.phonecat.infrastructure;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

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

    public PhoneEntity() {}
}
