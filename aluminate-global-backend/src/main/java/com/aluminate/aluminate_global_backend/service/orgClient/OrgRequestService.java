package com.aluminate.aluminate_global_backend.service.orgClient;

import com.aluminate.aluminate_global_backend.config.ResponseWrapper;
import com.aluminate.aluminate_global_backend.config.client.DynamicOrgClient;
import com.aluminate.aluminate_global_backend.config.factory.DynamicFeignFactory;
import com.aluminate.aluminate_global_backend.dto.syncOrgTickets.OrgMainTicketAck;
import com.aluminate.aluminate_global_backend.model.Organization;
import com.aluminate.aluminate_global_backend.repository.OrganizationRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

/**
 * Service used to build a dynamic Feign client for a target organization and send
 * ticket acknowledgement payloads to that organization's backend.
 *
 * <p>Responsibilities:
 * - Resolve the target organization's base URL and API prefix from the database.
 * - Construct a dynamic Feign client for the resolved URL using {@code DynamicFeignFactory}.
 * - Forward the {@code OrgMainTicketAck} payload and interpret the wrapped response.
 *
 * Note: this class intentionally lets upstream client/Feign exceptions bubble as runtime
 * exceptions so callers can handle retries or fallback logic where appropriate.
 */
@Service
@RequiredArgsConstructor
public class OrgRequestService {
    private final OrganizationRepository organizationRepository;
    private final DynamicFeignFactory dynamicFeignFactory;
    private final Logger logger = LoggerFactory.getLogger(OrgRequestService.class);


    /**
     * Send a ticket acknowledgement payload to the organization identified by {@code orgId}.
     *
     * <p>Steps performed:
     * 1. Load the {@link Organization} record from the database.
     * 2. Construct the target backend URL from {@code organization.serverUrl} and
     *    {@code organization.apiPrefix}. The resulting URL will always have a single
     *    '/' between base URL and prefix.
     * 3. Build a {@link DynamicOrgClient} for that URL and invoke the remote endpoint.
     * 4. Return {@code true} when the remote service responds with a non-null
     *    {@code ResponseWrapper<Boolean>} whose {@code data} is {@code true}; otherwise
     *    return {@code false}.
     *
     * @param orgId  the database id of the target organization (must exist)
     * @param payload the acknowledgement payload to forward to the organization
     * @return {@code true} if the remote organization explicitly acknowledged success,
     *         {@code false} otherwise
     * @throws RuntimeException if the organization cannot be found or if the organization's
     *                          server URL or API prefix is not set. Note that underlying
     *                          HTTP/Feign client errors (for example timeouts, 4xx/5xx)
     *                          will also propagate as runtime exceptions from the client.
     */


    public Boolean send(Long orgId, OrgMainTicketAck payload) {

        // 1. Get correct URL from DB
        Organization organization = organizationRepository.findById(orgId)
                .orElseThrow(() -> new RuntimeException("Organization not found"));

        String backend_Url = "";

        if(organization.getServerUrl() != null && !organization.getServerUrl().isEmpty() && organization.getApiPrefix() != null && !organization.getApiPrefix().isEmpty()) {
            backend_Url = organization.getServerUrl();

            backend_Url += organization.getApiPrefix();
        } else {
            throw new RuntimeException("Organization server URL or API prefix is not set");
        }

        // 2. Build Feign client dynamically for THAT URL
        DynamicOrgClient client = dynamicFeignFactory.build(backend_Url);



        // 3. Send request using Feign
        ResponseWrapper<Boolean> response = client.sendTicketAckToOrg(payload);
        logger.info("Response from org server for orgId {}: {}", orgId, response.getData());
        return response != null && Boolean.TRUE.equals(response.getData());
    }
}
