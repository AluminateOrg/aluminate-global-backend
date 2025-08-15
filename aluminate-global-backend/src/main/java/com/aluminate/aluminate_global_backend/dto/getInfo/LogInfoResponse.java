package com.aluminate.aluminate_global_backend.dto.getInfo;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Data
public class LogInfoResponse {
    private UserDTO admin;
    private OrganizationDTO organization;
    private String token;
}
