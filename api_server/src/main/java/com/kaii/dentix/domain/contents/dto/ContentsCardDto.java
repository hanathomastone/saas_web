package com.kaii.dentix.domain.contents.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter @Builder
@AllArgsConstructor @NoArgsConstructor
public class ContentsCardDto {

    private int number;

    private String path;

}
