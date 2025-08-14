package com.aluminate.aluminate_global_backend.dto.login;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class EncryptedLoginRequest {
    private String payload; // Encrypted login request in JSON format

}
