package com.monie.xpress.buy_airtime.dtos;

import lombok.*;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class XpressAPIRequestDTO {

    private String requestId;

    private String uniqueCode;

    private Details details;
}
