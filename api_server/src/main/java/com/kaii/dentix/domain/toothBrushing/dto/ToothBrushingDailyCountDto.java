package com.kaii.dentix.domain.toothBrushing.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter @Builder
@AllArgsConstructor @NoArgsConstructor
public class ToothBrushingDailyCountDto {

    String date;
    Integer count;
}
