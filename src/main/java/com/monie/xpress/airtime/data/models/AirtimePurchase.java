package com.monie.xpress.airtime.data.models;

import com.monie.xpress.auth_config.user.data.models.User;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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

    @Enumerated(EnumType.STRING)
    private Status status;

    @ManyToOne(cascade = CascadeType.ALL)
    private User user;

    private LocalDateTime transactionTime;
}
