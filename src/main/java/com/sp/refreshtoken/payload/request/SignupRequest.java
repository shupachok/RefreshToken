package com.sp.refreshtoken.payload.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SignupRequest {
    @NotBlank
    @Size(min = 3,max = 20)
    String username;

    @NotBlank
    @Size(min = 3,max = 20)
    String password;

    @Size(min = 3,max = 20)
    @NotBlank
    String email;
}
