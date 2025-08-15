package com.aluminate.aluminate_global_backend.service.payment;

import com.aluminate.aluminate_global_backend.dto.payment.PaymentNotifyRequest;

import com.aluminate.aluminate_global_backend.model.*;
import com.aluminate.aluminate_global_backend.repository.AdminRepository;
import com.aluminate.aluminate_global_backend.repository.OrganizationRepository;
import com.aluminate.aluminate_global_backend.repository.SubscriptionPlanRepository;
import com.aluminate.aluminate_global_backend.repository.TransactionRepository;
import com.aluminate.aluminate_global_backend.service.kafka.EventPublisherService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;

import java.math.BigDecimal;
import java.security.MessageDigest;
import java.util.Objects;
import java.util.Optional;

@Service
public class PaymentNotifyService {
    @Value("${payhere.merchant_secret}")
    private String merchantSecret;
    @Value("${payhere.merchant_id}")
    private String merchantId;

    private final TransactionRepository transactionRepository;
    private final OrganizationRepository organizationRepository;
    private final AdminRepository adminRepository;
    private final SubscriptionPlanRepository subscriptionPlanRepository;
    private final EventPublisherService eventPublisherService;

    private final Logger log = LoggerFactory.getLogger(PaymentNotifyService.class);

    public PaymentNotifyService(TransactionRepository transactionRepository,
                                OrganizationRepository organizationRepository,
                                AdminRepository adminRepository,
                                SubscriptionPlanRepository subscriptionPlanRepository,
                                EventPublisherService eventPublisherService
    ) {
        this.transactionRepository = transactionRepository;
        this.organizationRepository = organizationRepository;
        this.adminRepository = adminRepository;
        this.subscriptionPlanRepository = subscriptionPlanRepository;
        this.eventPublisherService = eventPublisherService;
    }


    @Transactional
    public void handlePaymentNotification(PaymentNotifyRequest request) {

        try {
            String localSig = generateMd5Sig(
                    request.getMerchant_id(),
                    request.getOrder_id(),
                    request.getPayhere_amount(),
                    request.getPayhere_currency(),
                    request.getStatus_code()

            );

            if (localSig.equals(request.getMd5sig()) && "2".equals(request.getStatus_code())) {
                // Payment verified and successful
                // update transaction by getting order_id(id in Transaction model)


                    Optional<Transaction> optionalTransaction = transactionRepository.findById(Long.valueOf(request.getOrder_id()));

                    if (optionalTransaction.isPresent()) {
                        Transaction transaction = getTransaction(request, optionalTransaction);
                        if(transaction == null) {
                            log.warn("Transaction is null for order ID: {}", request.getOrder_id());
                            return;
                        }

                        transactionRepository.save(transaction);
                        log.info("Transaction saved! Order ID: {}", request.getOrder_id());
                        // Publish event to Kafka or any other message broker if needed



                    } else {
                        log.warn("Transaction not found for ID: {}", request.getOrder_id());
                    }


            } else {
                log.warn("Payment verification FAILED for order {}", request.getOrder_id());
            }
        } catch (Exception e) {
            log.error("Error while verifying PayHere payment notification", e);
        }
    }

    private Transaction getTransaction(PaymentNotifyRequest request, Optional<Transaction> optionalTransaction) {
        try{
            Transaction transaction = optionalTransaction.get();

            transaction.setAmount(new BigDecimal(request.getPayhere_amount()));
            transaction.setPaymentId(request.getPayment_id());
            transaction.setMethod(request.getMethod());
            transaction.setStatusCode(request.getStatus_code());
            transaction.setStatusMessage(request.getStatus_message());
            transaction.setCardHolderName(Objects.requireNonNullElse(request.getCard_holder_name(), ""));
            transaction.setCardNo(Objects.requireNonNullElse(request.getCard_no(), ""));
            transaction.setTransactionStatus(TransactionStatus.SUCCESS);

            // Get admin email from custom_2
            String adminEmail = request.getCustom_2();
            if (adminEmail == null || adminEmail.isBlank()) {
                log.warn("Missing admin email in custom_2 for order {}", request.getOrder_id());
                return transaction;
            }

            // Fetch admin and set in transaction
            Optional<Object> optionalAdmin = adminRepository.findByEmail(adminEmail);
            if (optionalAdmin.isEmpty()) {
                log.warn("Admin not found for email: {}", adminEmail);
                return transaction;
            }

            Admin admin = (Admin) optionalAdmin.get();
            transaction.setAdmin(admin);

            // Fetch organization from admin
            Organization organization = admin.getOrganization();
            if (organization == null) {
                log.warn("Organization is null for admin with email: {}", adminEmail);
            } else {
                transaction.setOrganization(organization);
            }

            // Get subscription plan from custom_1
            String subscriptionPlanId = request.getCustom_1();
            Optional<SubscriptionPlan> subscriptionPlanOptional = subscriptionPlanRepository.findById(Long.valueOf(subscriptionPlanId));
            if (subscriptionPlanOptional.isEmpty() || subscriptionPlanId.isBlank()) {
                log.warn("Subscription plan not found for ID: {}", subscriptionPlanId);
                return transaction;
            }
            SubscriptionPlan subscriptionPlan = subscriptionPlanOptional.get();

            transaction.setSubscription_plan(subscriptionPlan.getName());

            //update organization with new plan
            updateOrganizationWithSubscriptionPlan(organization, subscriptionPlan);

            return transaction;
        }catch (Exception e){
            log.error("Error while verifying PayHere payment notification", e);
            return null;
        }


    }

    private void updateOrganizationWithSubscriptionPlan(Organization organization, SubscriptionPlan subscriptionPlan) {
        organization.setSubscriptionPlan(subscriptionPlan);
        organization.setMaxMemberCount(subscriptionPlan.getMemberLimit());
        organization.setNextRenewalDate(
                organization.getCreatedAt().toLocalDate().plusMonths(subscriptionPlan.getDurationInMonths())
        );
        organizationRepository.save(organization);
    }

    private String generateMd5Sig(String merchantId, String orderId, String amount, String currency, String statusCode) throws Exception {
        String localSecretHash = md5(merchantSecret).toUpperCase();
        String raw = merchantId + orderId + amount + currency + statusCode + localSecretHash;
        return md5(raw).toUpperCase();
    }

    private String md5(String input) throws Exception {
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] digest = md.digest(input.getBytes());
        StringBuilder sb = new StringBuilder();
        for (byte b : digest) {
            sb.append(String.format("%02x", b & 0xff));
        }
        return sb.toString();
    }
}
