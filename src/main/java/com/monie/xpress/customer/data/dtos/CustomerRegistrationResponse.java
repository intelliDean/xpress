package com.monie.xpress.customer.data.dtos;

import com.monie.xpress.auth_config.security.auth_utils.XpressAuthToken;
import lombok.*;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerRegistrationResponse {

    private String message;

    private XpressAuthToken xpressAuthToken;
}
