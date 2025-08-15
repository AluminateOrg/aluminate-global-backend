package com.aluminate.aluminate_global_backend.dto.org;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GlobalAuthResponse {
    private boolean success;
    private AdminGlobalDTO admin;
    private OrganizationGlobalDTO organization;

}
