package uz.eskiz.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import uz.eskiz.config.properties.EskizProperties;
import uz.eskiz.dto.request.CommonResultData;
import uz.eskiz.dto.request.SendSmsDispatchDTO;
import uz.eskiz.dto.response.SendSmsResponseDTO;
import uz.eskiz.dto.response.SendSmsResponseDispatchDTO;
import uz.eskiz.service.SendSmsService;
import uz.eskiz.storage.TokenStorage;
import uz.eskiz.util.HelperUtil;

import java.util.Optional;

import static uz.eskiz.enums.Constants.BEARER_TOKEN;
import static uz.eskiz.enums.Constants.SEND_SMS;
import static uz.eskiz.enums.Constants.SEND_SMS_DISPATCH;
import static uz.eskiz.util.HelperUtil.sendSmsFormData;

@Slf4j
@RequiredArgsConstructor
public class SendSmsServiceImpl implements SendSmsService {

    private final WebClient webClient;
    private final TokenStorage tokenStorage;
    private final EskizProperties properties;

    @Override
    public CommonResultData<SendSmsResponseDTO> sendSms(String phone, String text) {
        log.info("## Try to send sms by phone: {}, text: {}", phone, text);
        try {
            return fetchSendSms(phone, text)
                    .map(CommonResultData::success)
                    .orElseGet(() -> {
                        log.warn("Received empty result for send SMS");
                        return CommonResultData.failed("Failed to send SMS");
                    });
        } catch (RuntimeException e) {
            log.error("Send sms method handle exception: {}", e.getMessage(), e);
            return CommonResultData.failed("Exception occurred while sending SMS: " + e.getMessage());
        }
    }

    @Override
    public CommonResultData<SendSmsResponseDispatchDTO> sendSmsDispatch(SendSmsDispatchDTO request) {
        log.info("## Try to send sms dispatch by request: {}", request);
        try {
            return fetchSendSmsDispatch(request)
                    .map(CommonResultData::success)
                    .orElseGet(() -> {
                        log.warn("Received empty result for send SMS dispatch");
                        return CommonResultData.failed("Failed to send SMS dispatch");
                    });
        } catch (RuntimeException e) {
            log.error("Send sms dispatch method handle exception: {}", e.getMessage(), e);
            return CommonResultData.failed("Exception occurred while sending SMS dispatch: " + e.getMessage());
        }
    }

    private Optional<SendSmsResponseDispatchDTO> fetchSendSmsDispatch(SendSmsDispatchDTO request) {
        return Optional.ofNullable(webClient.post()
                .uri(SEND_SMS_DISPATCH)
                .header(HttpHeaders.AUTHORIZATION, BEARER_TOKEN + getToken())
                .header("Content-Type", "application/json")
                .bodyValue(request)
                .retrieve()
                .onStatus(HttpStatusCode::isError, HelperUtil::handleErrorResponse)
                .bodyToMono(SendSmsResponseDispatchDTO.class)
                .block());
    }

    private Optional<SendSmsResponseDTO> fetchSendSms(String phone, String text) {
        return Optional.ofNullable(webClient.post()
                .uri(SEND_SMS)
                .header(HttpHeaders.AUTHORIZATION, BEARER_TOKEN + getToken())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData(sendSmsFormData(phone, text,
                        properties.getSenderName(), properties.getCallbackUrl())))
                .retrieve()
                .onStatus(HttpStatusCode::isError, HelperUtil::handleErrorResponse)
                .bodyToMono(SendSmsResponseDTO.class)
                .block());
    }

    private String getToken() {
        return tokenStorage.getToken();
    }


}
