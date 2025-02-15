package com.sp.refreshtoken.payload.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SigninRequest {
    @NotBlank
    String username;
    @NotBlank
    String password;
}
