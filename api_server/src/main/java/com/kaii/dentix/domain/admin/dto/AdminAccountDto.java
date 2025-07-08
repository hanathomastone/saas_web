package com.kaii.dentix.domain.admin.dto;

import com.kaii.dentix.domain.type.YnType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter @Builder
@AllArgsConstructor @NoArgsConstructor
public class AdminAccountDto {

    private Long adminId;

    private String adminLoginIdentifier;

    private String adminName;

    private String adminPhoneNumber;

    private String created;

}
