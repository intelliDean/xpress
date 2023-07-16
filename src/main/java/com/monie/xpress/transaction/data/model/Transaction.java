package com.monie.xpress.transaction.data.model;

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
@Table(name = "transaction")
public class Transaction {

     @Id
    @GeneratedValue
    private Long id;

    private TransactionType transactionType;

    private BigDecimal amount;

    private LocalDateTime transactionTime;

    @ManyToOne(cascade = CascadeType.ALL)
    private User user;
}
