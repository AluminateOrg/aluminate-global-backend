package com.aluminate.aluminate_global_backend.dto.getInfo;



import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor

public class InfoResponse {
    private AdminDTO admin;
    private OrganizationDTO organization;

}
