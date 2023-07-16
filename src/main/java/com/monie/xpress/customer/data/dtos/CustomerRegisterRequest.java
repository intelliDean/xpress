package com.monie.xpress.customer.data.dtos;

import lombok.*;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerRegisterRequest {

    private String fullName;

    private String emailAddress;

    private String password;

    private String phoneNumber;
}
