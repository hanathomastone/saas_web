package com.kaii.dentix.domain.oralCheck.dto;

import lombok.*;

/**
 *  구강 검진 사진 결과
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OralCheckPhotoDto {
    private Long organizationId;
    private boolean success;
    private Integer remainingResponses;
    private Long oralCheckId; // 구강 검진 고유 번호
}
