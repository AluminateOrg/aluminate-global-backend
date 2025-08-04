package com.aluminate.aluminate_global_backend.dto.getInfo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SuperAdminDTO implements UserDTO{
    private long id;
    private String name;
    private String email;

}
