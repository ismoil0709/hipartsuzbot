package uz.eskiz.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import uz.eskiz.dto.request.CommonResultData;
import uz.eskiz.dto.response.smsInfo.SmsDataDTO;
import uz.eskiz.dto.response.smsInfo.SmsMessageDTO;
import uz.eskiz.dto.response.smsInfo.SmsResponseDTO;
import uz.eskiz.service.SmsStatusService;
import uz.eskiz.storage.TokenStorage;
import uz.eskiz.util.HelperUtil;

import java.util.Optional;

import static uz.eskiz.enums.Constants.AUTHORIZATION_HEADER;
import static uz.eskiz.enums.Constants.BEARER_TOKEN;
import static uz.eskiz.enums.Constants.GET_SMS_STATUS_BY_DISPATCH_ID;
import static uz.eskiz.enums.Constants.GET_SMS_STATUS_BY_SMS_ID;
import static uz.eskiz.util.HelperUtil.getInfoFormData;

@Slf4j
@RequiredArgsConstructor
public class SmsStatusServiceImpl implements SmsStatusService {
    private final WebClient webClient;
    private final TokenStorage tokenStorage;

    @Override
    public CommonResultData<SmsMessageDTO> getSmsStatusById(Long smsId) {
        log.info("## Attempting to retrieve SMS status with smsId: {} ##", smsId);
        try {
            return fetchSmsStatus(smsId)
                    .map(CommonResultData::success)
                    .orElseGet(() -> {
                        log.warn("Received empty result for SMS status");
                        return CommonResultData.failed("No SMS status found.");
                    });
        } catch (RuntimeException e) {
            log.error("WebClientException occurred while retrieving SMS status: {}", e.getMessage());
            return CommonResultData.failed("Failed to retrieve SMS status: " + e.getMessage());
        }
    }

    @Override
    public CommonResultData<SmsDataDTO> getDispatchStatusByDispatchId(String dispatchId) {
        log.info("## Attempting to retrieve SMS dispatch status with dispatchId: {} ##", dispatchId);
        try {
            return fetchSmsDispatchStatus(dispatchId)
                    .map(CommonResultData::success)
                    .orElseGet(() -> {
                        log.warn("Received empty result for SMS dispatch status");
                        return CommonResultData.failed("No SMS dispatch status found.");
                    });
        } catch (RuntimeException e) {
            log.error("WebClientException occurred while retrieving SMS dispatch Status: {}", e.getMessage());
            return CommonResultData.failed("Failed to retrieve templates: " + e.getMessage());
        }
    }

    private Optional<SmsMessageDTO> fetchSmsStatus(Long smsId) {
        return Optional.ofNullable(webClient.get()
                .uri(GET_SMS_STATUS_BY_SMS_ID + smsId)
                .header(AUTHORIZATION_HEADER, BEARER_TOKEN + getToken())
                .retrieve()
                .onStatus(HttpStatusCode::isError, HelperUtil::handleErrorResponse)
                .bodyToMono(new ParameterizedTypeReference<SmsResponseDTO<SmsMessageDTO>>() {
                })
                .map(SmsResponseDTO::getData)
                .block());
    }

    private Optional<SmsDataDTO> fetchSmsDispatchStatus(String dispatchId) {
        return Optional.ofNullable(webClient.post()
                .uri(GET_SMS_STATUS_BY_DISPATCH_ID)
                .header(AUTHORIZATION_HEADER, BEARER_TOKEN + getToken())
                .body(BodyInserters.fromFormData(getInfoFormData(dispatchId)))
                .retrieve()
                .onStatus(HttpStatusCode::isError, HelperUtil::handleErrorResponse)
                .bodyToMono(new ParameterizedTypeReference<SmsResponseDTO<SmsDataDTO>>() {
                })
                .map(SmsResponseDTO::getData)
                .block());
    }

    private String getToken() {
        return tokenStorage.getToken();
    }

}
