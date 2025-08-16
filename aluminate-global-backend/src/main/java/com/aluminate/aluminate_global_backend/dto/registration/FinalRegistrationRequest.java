package com.aluminate.aluminate_global_backend.dto.registration;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FinalRegistrationRequest {
    @NotBlank(message = "cannot be blank")
    private String organizationName;

    @NotBlank(message = "cannot be blank")
    private String adminFullName;

    @NotBlank(message = "cannot be blank")
    private String email;

    @NotBlank(message = "cannot be blank")
    private String phoneNumber;

    @NotBlank(message = "cannot be blank")
    private String password;

    @NotBlank(message = "cannot be blank")
    private String nationalId;
}
