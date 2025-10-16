package com.aluminate.aluminate_global_backend.dto.otp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@AllArgsConstructor
public class OtpRequest {
    private String email;
    private String otp;
}
