package uz.eskiz.service.impl;


import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import uz.eskiz.dto.request.CommonResultData;
import uz.eskiz.dto.request.TemplateRequestDTO;
import uz.eskiz.dto.response.EskizResponseDTO;
import uz.eskiz.dto.response.TemplateResponseDTO;
import uz.eskiz.service.TemplateService;
import uz.eskiz.storage.TokenStorage;
import uz.eskiz.util.HelperUtil;

import java.util.List;
import java.util.Optional;

import static uz.eskiz.enums.Constants.AUTHORIZATION_HEADER;
import static uz.eskiz.enums.Constants.BEARER_TOKEN;
import static uz.eskiz.enums.Constants.GET_SMS_TEMPLATE;
import static uz.eskiz.enums.Constants.SEND_TEMPLATE;
import static uz.eskiz.util.HelperUtil.formDataTemplate;

@Slf4j
@RequiredArgsConstructor
public class TemplateServiceImpl implements TemplateService {

    private final WebClient webClient;
    private final TokenStorage tokenStorage;

    @Override
    public CommonResultData<List<TemplateResponseDTO>> getTemplateData() {
        log.info("## Attempting to retrieve user templates ##");
        try {
            return fetchGetTemplate()
                    .map(CommonResultData::success)
                    .orElseGet(() -> {
                        log.warn("Received empty result for get templates");
                        return CommonResultData.failed("Failed to get template");
                    });
        } catch (RuntimeException e) {
            log.error("WebClientException occurred while get template: {}", e.getMessage());
            return CommonResultData.failed("Failed to get template: " + e.getMessage());
        }
    }

    @Override
    public CommonResultData<Long> createTemplate(TemplateRequestDTO request) {
        log.info("## Attempting to create template with body: {}", request);
        try {
            return fetchCreateTemplate(request)
                    .map(CommonResultData::success)
                    .orElseGet(() -> {
                        log.warn("Received empty result for create template");
                        return CommonResultData.failed("Failed to create template");
                    });
        } catch (RuntimeException e) {
            log.error("WebClientException occurred while creating template: {}", e.getMessage());
            return CommonResultData.failed("Failed to create template: " + e.getMessage());
        }
    }

    private Optional<Long> fetchCreateTemplate(TemplateRequestDTO request) {
        return Optional.ofNullable(webClient.post()
                .uri(SEND_TEMPLATE)
                .header(AUTHORIZATION_HEADER, BEARER_TOKEN + getToken())
                .body(BodyInserters.fromFormData(formDataTemplate(request.getTemplate())))
                .retrieve()
                .onStatus(HttpStatusCode::isError, HelperUtil::handleErrorResponse)
                .bodyToMono(JsonNode.class)
                .map(jsonNode -> jsonNode.path("data").path("id").asLong())
                .block());
    }


    private Optional<List<TemplateResponseDTO>> fetchGetTemplate() {
        return Optional.ofNullable(webClient.post()
                .uri(GET_SMS_TEMPLATE)
                .header(AUTHORIZATION_HEADER, BEARER_TOKEN + getToken())
                .retrieve()
                .onStatus(HttpStatusCode::isError, HelperUtil::handleErrorResponse)
                .bodyToMono(EskizResponseDTO.class)
                .map(EskizResponseDTO::getResult)
                .block());
    }

    private String getToken() {
        return tokenStorage.getToken();
    }
}

