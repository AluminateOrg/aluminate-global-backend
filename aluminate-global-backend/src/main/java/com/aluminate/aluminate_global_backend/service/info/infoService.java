package com.aluminate.aluminate_global_backend.service.info;


import com.aluminate.aluminate_global_backend.dto.getInfo.AdminDTO;
import com.aluminate.aluminate_global_backend.dto.getInfo.InfoResponse;
import com.aluminate.aluminate_global_backend.dto.getInfo.OrganizationDTO;
import com.aluminate.aluminate_global_backend.model.Admin;
import com.aluminate.aluminate_global_backend.model.Organization;
import com.aluminate.aluminate_global_backend.repository.AdminRepository;
import com.aluminate.aluminate_global_backend.repository.OrganizationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class infoService {
    private final OrganizationRepository organizationRepository;
    private final AdminRepository adminRepository;
    private final Logger logger = LoggerFactory.getLogger(infoService.class);
    public infoService(OrganizationRepository organizationRepository, AdminRepository adminRepository) {
        this.organizationRepository = organizationRepository;
        this.adminRepository = adminRepository;
    }

    public InfoResponse getUser(){

        try{
            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (!(principal instanceof Admin admin)) {
                logger.error("Principal is not Admin");
                throw new IllegalStateException("Authenticated principal is not an Admin");
            }


            Organization org = admin.getOrganization();

            AdminDTO adminDTO = new AdminDTO(
                    admin.getId(),
                    admin.getName(),
                    admin.getEmail(),
                    admin.getPhone(),
                    admin.isEmailVerified()
            );

            String subscriptionPlan = org.getSubscriptionPlan() != null ? org.getSubscriptionPlan().getName() : "No Plan";

            OrganizationDTO orgDTO = new OrganizationDTO(
                    org.getId(),
                    org.getOrganizationName(),
                    subscriptionPlan,
                    org.getCreatedAt(),
                    org.getNextRenewalDate(),
                    org.getSubdomain(),
                    org.getPortalUrl(),
                    org.getMaxMemberCount(),
                    org.getCurrentMemberCount(),
                    org.getStatus()
            );


            return new InfoResponse(adminDTO, orgDTO);
        }catch (Exception e){
            logger.error(e.getMessage());
            throw new IllegalStateException(e);
        }

    }
}
