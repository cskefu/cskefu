package com.cskefu.security;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class LoginRequest {
    @NotEmpty
    @Length(min = 5, max = 15)
    private String username;

    @NotEmpty
    @Length(min = 6, max = 20)
    private String password;
    private String captcha;
    private String uuid;
}
