package com.aluminate.aluminate_global_backend.dto.login;

import com.aluminate.aluminate_global_backend.dto.org.AdminOrgDTO;
import com.aluminate.aluminate_global_backend.dto.org.OrganizationOrgDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Data
public class LoginOrgResponse {
    private AdminOrgDTO admin;
    private OrganizationOrgDTO organization;


}
