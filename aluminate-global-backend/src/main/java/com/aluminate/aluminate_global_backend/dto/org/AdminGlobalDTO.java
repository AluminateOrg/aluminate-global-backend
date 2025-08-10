package com.aluminate.aluminate_global_backend.dto.org;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class AdminGlobalDTO {

    private String name;
    private String email;
    private String password;
    private String phone;

}

