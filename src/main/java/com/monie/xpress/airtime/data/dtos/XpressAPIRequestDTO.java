package com.monie.xpress.airtime.data.dtos;

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
