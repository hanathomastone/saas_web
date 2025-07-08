package com.kaii.dentix.domain.oralCheck.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter @Builder
@AllArgsConstructor @NoArgsConstructor
public class OralCheckAnalysisDivisionDto {

    private Float topRight; // 우상 영역

    private Float topLeft; // 좌상 영역

    private Float btmRight; // 우하 영역

    private Float btmLeft; // 좌하 영역

}
