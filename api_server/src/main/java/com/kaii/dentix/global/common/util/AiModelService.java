package com.kaii.dentix.global.common.util;

import com.kaii.dentix.domain.oralCheck.dto.resoponse.OralCheckAnalysisResponse;
import com.kaii.dentix.domain.questionnaire.dto.response.QuestionnaireAnalysisResponse;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Service
public class AiModelService {

    @Value("${aiModel.apiUrl.oralCheck}")
    private String oralCheckAiModelApiUrl;

    @Value("${aiModel.apiUrl.questionnaire}")
    private String questionnaireAiModelApiUrl;

    private final RestTemplate restTemplate;

    public AiModelService(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    /**
     *  구강검진 사진 촬영 AI Model
     */
    @SneakyThrows
    @Async
    public OralCheckAnalysisResponse getPyDentalAiModel(MultipartFile picture) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();

        ByteArrayResource fileResource = new ByteArrayResource(picture.getBytes()) {
            @Override
            public String getFilename() {
                return picture.getOriginalFilename();
            }
        };

        params.add("picture", fileResource);

        HttpEntity<MultiValueMap<String, Object>> entity = new HttpEntity<>(params, headers);

        return restTemplate.postForObject(oralCheckAiModelApiUrl, entity, OralCheckAnalysisResponse.class);
    }

    /**
     *  문진표 AI Model
     */
    @SneakyThrows
    @Async
    public QuestionnaireAnalysisResponse getQuestionnaireAiModel(Map<String, Map<String, Object>> survey) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
        params.add("survey", survey);

        HttpEntity<MultiValueMap<String, Object>> entity = new HttpEntity<>(params, headers);

        return restTemplate.postForObject(questionnaireAiModelApiUrl, entity, QuestionnaireAnalysisResponse.class);
    }

}
