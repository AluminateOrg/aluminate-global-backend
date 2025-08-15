package com.aluminate.aluminate_global_backend.dto.getInfo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminDTO implements UserDTO{
    private Long id;
    private String name;
    private String email;
    private String phone;
    private boolean emailVerified;
}
