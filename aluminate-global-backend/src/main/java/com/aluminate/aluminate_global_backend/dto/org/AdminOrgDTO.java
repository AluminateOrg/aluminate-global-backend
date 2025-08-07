package com.aluminate.aluminate_global_backend.dto.org;

import com.aluminate.aluminate_global_backend.dto.getInfo.UserDTO;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class AdminOrgDTO {

    private String name;
    private String email;
    private String password;
    private String phone;
    private boolean emailVerified;
}

