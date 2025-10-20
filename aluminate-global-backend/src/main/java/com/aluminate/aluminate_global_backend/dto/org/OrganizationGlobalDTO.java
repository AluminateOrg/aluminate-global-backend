package com.aluminate.aluminate_global_backend.dto.org;
import com.aluminate.aluminate_global_backend.model.Status;
import lombok.*;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class OrganizationGlobalDTO {
    private String organizationName;
    private int maxMemberCount;
    private int currentMemberCount;
    private Status status;
    private boolean isMembershipFree;
    private boolean isDeleted;
}
