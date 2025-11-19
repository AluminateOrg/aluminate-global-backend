package com.aluminate.aluminate_global_backend.controller.organisation;

import com.aluminate.aluminate_global_backend.config.ResponseWrapper;
import com.aluminate.aluminate_global_backend.dto.payment.PaymentUpdateRequest;
import com.aluminate.aluminate_global_backend.service.organization.OrgService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("{api.prefix}/public")
public class OrgController {

    private final OrgService orgService;

    public OrgController(OrgService orgService) {
        this.orgService = orgService;
    }

    @PostMapping("/update-org")
    public ResponseEntity<ResponseWrapper<Boolean>> updateOrg(PaymentUpdateRequest request, @PathVariable String api) {
        try {
            boolean isUpdated = orgService.updateOrganization(request.getSubscriptionPlanId(), request.getOrgId());
            if (isUpdated) {
                return ResponseEntity.ok(new ResponseWrapper<>(true, "Organization updated successfully", true));
            } else {
                return ResponseEntity.ok(new ResponseWrapper<>(false, "Organization update failed", false));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
