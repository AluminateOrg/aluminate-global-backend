package com.aluminate.aluminate_global_backend.dto.login;

import com.aluminate.aluminate_global_backend.dto.org.AdminGlobalDTO;
import com.aluminate.aluminate_global_backend.dto.org.OrganizationGlobalDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Data
public class LoginOrgResponse {
    private AdminGlobalDTO admin;
    private OrganizationGlobalDTO organization;


}
