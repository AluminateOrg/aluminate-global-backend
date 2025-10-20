package com.aluminate.aluminate_global_backend.service;

import com.aluminate.aluminate_global_backend.model.Organization;
import com.aluminate.aluminate_global_backend.model.Status;
import com.aluminate.aluminate_global_backend.repository.OrganizationRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class OrgContainerAsyncService {

    private final OrgContainerService orgContainerService;
    private final OrganizationRepository organizationRepository;

    public OrgContainerAsyncService(OrgContainerService orgContainerService,
                                    OrganizationRepository organizationRepository) {
        this.orgContainerService = orgContainerService;
        this.organizationRepository = organizationRepository;
    }

    @Async
    public void buildOrgContainerAsync(Long orgId) {
        boolean success = orgContainerService.createOrgContainer(orgId.toString());
        Organization org = organizationRepository.findById(orgId)
                .orElseThrow(() -> new RuntimeException("Organization not found: " + orgId));

        if (success) {
            org.setStatus(Status.ACTIVE);
            // set subdomain and portalUrl
            String subdomain = "localhost" + "/" + orgId;
            String portalUrl = "http://" + subdomain ;
            org.setSubdomain(subdomain);
            org.setPortalUrl(portalUrl);
        } else {
            org.setStatus(Status.BUILD_FAILED);
        }
        organizationRepository.save(org);
    }
}

