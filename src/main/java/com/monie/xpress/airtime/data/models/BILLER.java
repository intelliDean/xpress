package com.monie.xpress.airtime.data.models;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum BILLER {

    MTN("MTN_24207"),

    GLO("GLO_30387"),

    AIRTEL("AIRTEL_22689"),

    ETISALAT("9MOBILE_69358");

    private final String uniqueCode;
}