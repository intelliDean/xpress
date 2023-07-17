package com.monie.xpress.auth_config.user.data.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import static com.monie.xpress.xpress_utils.XpressConstants.NOT_BLANK;
import static com.monie.xpress.xpress_utils.XpressConstants.NOT_NULL;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {

    @NotNull(message = NOT_NULL)
    @NotBlank(message = NOT_BLANK)
    private String emailAddress;

    @NotNull(message = NOT_NULL)
    @NotBlank(message = NOT_BLANK)
    private String password;
}
