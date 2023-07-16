package com.monie.xpress.buy_airtime.dtos;

import lombok.*;

import java.math.BigDecimal;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseAirtimeDTO {

    private String phoneNumber;

    private BigDecimal amount;
}
