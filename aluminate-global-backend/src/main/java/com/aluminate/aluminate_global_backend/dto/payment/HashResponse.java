package com.aluminate.aluminate_global_backend.dto.payment;

import lombok.*;

@Setter
@Getter
@Data
@AllArgsConstructor
@NoArgsConstructor
public class HashResponse {
    private String hash;
    private Long transaction_id;

    public HashResponse(String organizationOrAdminNotFound) {
    }
}
