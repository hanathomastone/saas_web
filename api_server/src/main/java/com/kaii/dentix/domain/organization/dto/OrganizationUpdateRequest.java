package com.kaii.dentix.domain.organization.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrganizationUpdateRequest {
    private String organizationName;
    private Long subscriptionPlanId;
}