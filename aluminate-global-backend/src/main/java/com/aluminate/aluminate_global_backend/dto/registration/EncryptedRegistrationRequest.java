package com.aluminate.aluminate_global_backend.dto.registration;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EncryptedRegistrationRequest {
    private String payload; // Encrypted registration request in JSON format
    private RegistrationRequest obj;
}
