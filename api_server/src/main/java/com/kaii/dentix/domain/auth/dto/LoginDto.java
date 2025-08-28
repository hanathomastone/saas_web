package com.kaii.dentix.domain.auth.dto;


import com.kaii.dentix.domain.type.YnType;
import com.kaii.dentix.domain.user.dto.TokenDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class LoginDto extends TokenDto {

    // 공통 필드 (USER/ADMIN 모두)
    private Long id;         // userId or adminId
    private String name;     // userName or adminName
    private String userType; // "USER" or "ADMIN"

    // 관리자만 해당 (USER 로그인시 null)
    private YnType isFirstLogin;
    private YnType adminIsSuper;
}