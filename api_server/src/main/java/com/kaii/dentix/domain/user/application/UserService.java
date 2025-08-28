package com.kaii.dentix.domain.user.application;

import com.kaii.dentix.domain.findPwdQuestion.dao.FindPwdQuestionRepository;
import com.kaii.dentix.domain.jwt.JwtTokenUtil;
import com.kaii.dentix.domain.jwt.TokenType;
import com.kaii.dentix.domain.patient.dao.PatientRepository;
import com.kaii.dentix.domain.patient.domain.Patient;
import com.kaii.dentix.domain.serviceAgreement.dao.ServiceAgreementCustomRepository;
import com.kaii.dentix.domain.serviceAgreement.dao.ServiceAgreementRepository;
import com.kaii.dentix.domain.serviceAgreement.domain.ServiceAgreement;
import com.kaii.dentix.domain.type.DeviceType;
import com.kaii.dentix.domain.type.UserRole;
import com.kaii.dentix.domain.type.YnType;
import com.kaii.dentix.domain.user.dao.UserRepository;
import com.kaii.dentix.domain.user.domain.User;
import com.kaii.dentix.domain.user.dto.UserInfoDto;
import com.kaii.dentix.domain.user.dto.UserInfoModifyDto;
import com.kaii.dentix.domain.user.dto.UserInfoModifyQnADto;
import com.kaii.dentix.domain.user.dto.UserLoginDto;
import com.kaii.dentix.domain.user.dto.request.*;
import com.kaii.dentix.domain.user.event.UserModifyDeviceInfoEvent;
import com.kaii.dentix.domain.userServiceAgreement.dao.UserServiceAgreementRepository;
import com.kaii.dentix.domain.userServiceAgreement.domain.UserServiceAgreement;
import com.kaii.dentix.domain.userServiceAgreement.dto.UserModifyServiceAgreeDto;
import com.kaii.dentix.domain.userServiceAgreement.dto.UserServiceAgreeList;
import com.kaii.dentix.domain.userServiceAgreement.dto.request.UserModifyServiceAgreeRequest;
import com.kaii.dentix.global.common.error.exception.*;
import io.micrometer.common.util.StringUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    private final JwtTokenUtil jwtTokenUtil;

    private final ApplicationEventPublisher publisher;

//    private final UserDeviceTypeRepository userDeviceTypeRepository;

    private final PasswordEncoder passwordEncoder;

    private final FindPwdQuestionRepository findPwdQuestionRepository;

    private final UserServiceAgreementRepository userServiceAgreementRepository;

    private final ServiceAgreementRepository serviceAgreementRepository;

    private final PatientRepository patientRepository;

    private final ServiceAgreementCustomRepository serviceAgreementCustomRepository;


    /**
     * 토큰에서 User 추출
     */
    public User getTokenUser(HttpServletRequest servletRequest) {

        String token = jwtTokenUtil.getAccessToken(servletRequest);

        UserRole roles = jwtTokenUtil.getRoles(token, TokenType.AccessToken);
        if (!roles.equals(UserRole.ROLE_USER)) throw new UnauthorizedException("권한이 없는 사용자입니다.");

        Long userId = jwtTokenUtil.getUserId(token, TokenType.AccessToken);
        return userRepository.findById(userId).orElseThrow(() -> new NotFoundDataException("존재하지 않는 사용자입니다."));

    }

    /**
     * 토큰에서 User 추출 - 토큰 NULL 허용
     */
    public User getTokenUserNullable(HttpServletRequest servletRequest) {

        String token = jwtTokenUtil.getAccessToken(servletRequest);

        if (StringUtils.isBlank(token)){ // 비로그인 사용자
            return null;
        }

        if (jwtTokenUtil.isExpired(token, TokenType.AccessToken)) throw new TokenExpiredException();

        UserRole roles = jwtTokenUtil.getRoles(token, TokenType.AccessToken);
        if (!roles.equals(UserRole.ROLE_USER)) throw new UnauthorizedException("권한이 없는 사용자입니다.");

        Long userId = jwtTokenUtil.getUserId(token, TokenType.AccessToken);
        return userRepository.findById(userId).orElseThrow(() -> new NotFoundDataException("존재하지 않는 사용자입니다."));

    }


    /**
     *  사용자 자동 로그인
     */
    @Transactional
    public UserLoginDto userAutoLogin(HttpServletRequest httpServletRequest, UserAutoLoginRequest userAutoLoginRequest){
        User user = this.getTokenUser(httpServletRequest);

        String accessToken = jwtTokenUtil.createToken(user, TokenType.AccessToken);
        String refreshToken = jwtTokenUtil.createToken(user, TokenType.RefreshToken);

        user.updateLogin(refreshToken);

        publisher.publishEvent(new UserModifyDeviceInfoEvent(
                user.getUserId(),
                httpServletRequest

        ));

        return UserLoginDto.builder()
                .userId(user.getUserId())
                .userName(user.getUserName())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();

    }

    /**
     *  사용자 비밀번호 확인
     */
    @Transactional
    public void userPasswordVerify(HttpServletRequest httpServletRequest, UserPasswordVerifyRequest request){
        User user = this.getTokenUser(httpServletRequest);

        if (!passwordEncoder.matches(request.getUserPassword(), user.getUserPassword())){
            throw new UnauthorizedException("비밀번호가 일치하지 않습니다.");
        }

    }

    /**
     *  사용자 보안정보수정 - 비밀번호 변경
     */
    @Transactional
    public void userModifyPassword(HttpServletRequest httpServletRequest, UserInfoModifyPasswordRequest request){
        User user = this.getTokenUser(httpServletRequest);
        user.modifyUserPassword(passwordEncoder, request.getUserPassword());
    }

    /**
     *  사용자 보안정보수정 - 질문과 답변 수정
     */
    @Transactional
    public UserInfoModifyQnADto userModifyQnA(HttpServletRequest httpServletRequest, UserInfoModifyQnARequest request) {
        User user = this.getTokenUser(httpServletRequest);

        // 올바르지 않은 findPwdQuestionId 인 경우
        if (!findPwdQuestionRepository.findById(request.getFindPwdQuestionId()).isPresent()) throw new NotFoundDataException("존재하지 않는 질문입니다.");

        user.modifyQnA(request.getFindPwdQuestionId(), request.getFindPwdAnswer());

        return UserInfoModifyQnADto.builder()
                .findPwdQuestionId(user.getFindPwdQuestionId())
                .findPwdAnswer(user.getFindPwdAnswer())
                .build();

    }

    /**
     *  사용자 회원 정보 수정
     */
    @Transactional
    public UserInfoModifyDto userModifyInfo(HttpServletRequest httpServletRequest, UserInfoModifyRequest request){
        User user = this.getTokenUser(httpServletRequest);

        user.modifyInfo(request.getUserName(), request.getUserGender());

        return UserInfoModifyDto.builder()
                .userName(user.getUserName())
                .userGender(user.getUserGender())
                .build();
    }

    /**
     *  사용자 서비스 이용동의 여부 수정
     */
    @Transactional
    public UserModifyServiceAgreeDto userModifyServiceAgree(HttpServletRequest httpServletRequest, UserModifyServiceAgreeRequest request){
        User user = this.getTokenUser(httpServletRequest);

        ServiceAgreement serviceAgreement = serviceAgreementRepository.findById(request.getServiceAgreeId()).orElseThrow(() -> new NotFoundDataException("존재하지 않는 서비스 이용 동의입니다."));
        if (serviceAgreement.getIsServiceAgreeRequired().equals(YnType.Y)) throw new BadRequestApiException("필수 항목은 수정할 수 없습니다.");

        UserServiceAgreement userServiceAgreement = userServiceAgreementRepository.findByServiceAgreeIdAndUserId(serviceAgreement.getServiceAgreeId(), user.getUserId()).orElse(null);

        if (userServiceAgreement == null) {
            userServiceAgreement = userServiceAgreementRepository.save(UserServiceAgreement.builder()
                    .userId(user.getUserId())
                    .serviceAgreeId(serviceAgreement.getServiceAgreeId())
                    .isUserServiceAgree(request.getIsUserServiceAgree())
                    .userServiceAgreeDate(new Date())
                    .build());
        } else {
            userServiceAgreement.modifyServiceAgree(request.getIsUserServiceAgree());
        }

        return UserModifyServiceAgreeDto.builder()
                .serviceAgreeId(userServiceAgreement.getServiceAgreeId())
                .isUserServiceAgree(userServiceAgreement.getIsUserServiceAgree())
                .date(userServiceAgreement.getUserServiceAgreeDate())
                .build();
    }

    /**
     *  사용자 회원정보 조회
     */
    public UserInfoDto userInfo(HttpServletRequest httpServletRequest){
        User user = this.getTokenUser(httpServletRequest);

        // 사용자 서비스 '선택' 이용 동의 여부 조회
        List<UserServiceAgreeList> serviceAgreementList = serviceAgreementCustomRepository.findAllByNotRequiredServiceAgreement(user.getUserId());

        String patientPhoneNumber = null;

        if (user.getPatientId() != null){
            Patient patient = patientRepository.findById(user.getPatientId()).orElseThrow(() -> new NotFoundDataException("존재하지 않는 환자입니다."));
            patientPhoneNumber = patient.getPatientPhoneNumber();
        }

        return UserInfoDto.builder()
                .userName(user.getUserName())
                .userLoginIdentifier(user.getUserLoginIdentifier())
                .patientPhoneNumber(patientPhoneNumber != null ? patientPhoneNumber : user.getIsVerify().equals(YnType.Y) ? "-" : null)
                .userServiceAgreeLists(serviceAgreementList)
                .userGender(user.getUserGender())
                .build();
    }

    /**
     *  사용자 로그아웃
     */
    @Transactional
    public void userLogout(HttpServletRequest httpServletRequest){
        User user = this.getTokenUser(httpServletRequest);
        user.logout();
    }

    /**
     *  사용자 회원탈퇴
     */
    @Transactional
    public void userRevoke(HttpServletRequest httpServletRequest){
        User user = this.getTokenUser(httpServletRequest);
        user.revoke();
    }


}
