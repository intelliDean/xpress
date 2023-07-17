package com.monie.xpress.airtime.data.dtos;

import lombok.*;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AirtimePurchaseResponse {
    private String referenceId;

    private String requestId;

    private String responseCode;

    private String responseMessage;

    private Object data;
}
