package com.aluminate.aluminate_global_backend.dto.getInfo;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LogInfoResponse {
    private AdminDTO admin;
    private OrganizationDTO organization;
    private String token;
}
