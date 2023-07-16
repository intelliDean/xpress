package com.monie.xpress.buy_airtime.models;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Setter
@Getter
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "airtime_purchase")
public class AirtimePurchase {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String uniqueCode;

    private String phoneNumber;

    private BigDecimal amount;

    private Status status;
}
