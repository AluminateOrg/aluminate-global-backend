package com.aluminate.aluminate_global_backend.service.organization;

import com.aluminate.aluminate_global_backend.model.Organization;
import com.aluminate.aluminate_global_backend.model.Status;
import com.aluminate.aluminate_global_backend.model.SubscriptionPlan;
import com.aluminate.aluminate_global_backend.repository.OrganizationRepository;
import com.aluminate.aluminate_global_backend.repository.SubscriptionPlanRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class OrgService {

    private final OrganizationRepository organizationRepository;
    private final SubscriptionPlanRepository subscriptionPlanRepository;

    public OrgService(OrganizationRepository organizationRepository, SubscriptionPlanRepository subscriptionPlanRepository) {
        this.organizationRepository = organizationRepository;
        this.subscriptionPlanRepository = subscriptionPlanRepository;
    }

    @Transactional
    public boolean updateOrganization(Long subsId, Long orgId) {
        Optional<Organization> organization = organizationRepository.findById(orgId);
        Optional<SubscriptionPlan> subscriptionPlan = subscriptionPlanRepository.findById(subsId);

        //update organisation features
        if (organization.isEmpty() || subscriptionPlan.isEmpty()) {
            return false;
        }

        Organization org = organization.get();
        SubscriptionPlan plan = subscriptionPlan.get();

        if (org.getCreatedAt() == null || plan.getDurationInMonths() == null) {
            return false;
        }

        org.setSubscriptionPlan(plan);
        org.setMaxMemberCount(plan.getMemberLimit());
        org.setNextRenewalDate(org.getCreatedAt().toLocalDate().plusMonths(plan.getDurationInMonths()));
        org.setStatus(Status.ACTIVE);
        organizationRepository.save(org);
        return true;
    }

}
