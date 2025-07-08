package com.kaii.dentix.domain.oralCheck.controller;

import com.kaii.dentix.domain.oralCheck.application.OralCheckService;
import com.kaii.dentix.domain.oralCheck.dto.DashboardDto;
import com.kaii.dentix.domain.oralCheck.dto.OralCheckDto;
import com.kaii.dentix.domain.oralCheck.dto.OralCheckPhotoDto;
import com.kaii.dentix.domain.oralCheck.dto.OralCheckResultDto;
import com.kaii.dentix.global.common.error.exception.FormValidationException;
import com.kaii.dentix.global.common.response.DataResponse;
import io.micrometer.common.util.StringUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/oralCheck")
public class OralCheckController {

    private final OralCheckService oralCheckService;

    /**
     * 구강검진 사진 촬영
     */
    @PostMapping(value = "/photo", name = "구강검진 사진 촬영")
    public DataResponse<OralCheckPhotoDto> oralCheckPhoto(HttpServletRequest httpServletRequest, @RequestParam MultipartFile file) throws IOException, NoSuchAlgorithmException, InvalidKeyException, InterruptedException {
        if (file.isEmpty() || StringUtils.isBlank(file.getContentType())) throw new FormValidationException("파일을 업로드해 주세요.");
        if (!file.getContentType().equals(MediaType.IMAGE_PNG_VALUE) && !file.getContentType().equals(MediaType.IMAGE_JPEG_VALUE)) throw new FormValidationException("png, jpg 형식만 업로드 가능합니다.");

        DataResponse<OralCheckPhotoDto> response = oralCheckService.oralCheckPhoto(httpServletRequest, file);
        return response;
    }

    /**
     *  구강검진 결과
     */
    @GetMapping(value = "/result", name = "구강검진 결과")
    public DataResponse<OralCheckResultDto> oralCheckResult(HttpServletRequest httpServletRequest, @RequestParam Long oralCheckId){
        DataResponse<OralCheckResultDto> response = new DataResponse<>(oralCheckService.oralCheckResult(httpServletRequest, oralCheckId));
        return response;
    }

    /**
     * 대시보드 조회
     */
    @GetMapping(value = "/dashboard", name = "대시보드 조회")
    public DataResponse<DashboardDto> dashboard(HttpServletRequest httpServletRequest){
        DataResponse<DashboardDto> response = new DataResponse<>(oralCheckService.dashboard(httpServletRequest));
        return response;
    }

    /**
     *  구강 상태 조회
     */
    @GetMapping(name = "구강 상태 조회")
    public DataResponse<OralCheckDto> oralCheck(HttpServletRequest httpServletRequest){
        DataResponse<OralCheckDto> response = new DataResponse<>(oralCheckService.oralCheck(httpServletRequest));
        return response;
    }
}
