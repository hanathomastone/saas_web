package com.kaii.dentix.global.common.aop.dto;

import com.kaii.dentix.domain.type.UserRole;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoggerDTO {

    private Long tokenUserId;
    private UserRole tokenUserRole;
    private String requestName;
    private String requestUrl;
    private String header;
    private String requestBody;

    @Override
    public String toString() {
        return "{"
            + "\"tokenUserId\":" + tokenUserId
            + ", \"tokenUserRole\":\"" + tokenUserRole + "\""
            + ", \"requestName\":\"" + requestName + "\""
            + ", \"requestUrl\":\"" + requestUrl + "\""
            + ", \"header\":\"" + header + "\""
            + ", \"requestBody\":\"" + requestBody + "\""
            + "}";
    }
}
