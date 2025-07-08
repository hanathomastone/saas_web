package com.kaii.dentix.domain.type;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum UserRole {

    ROLE_USER(1, "회원 권한", "사용자"),

    ROLE_ADMIN(2, "회원 권한", "관리자");

    private final int id;
    private final String description;
    private final String value;

}
