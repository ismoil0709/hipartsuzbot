package uz.eskiz.util;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.ClientResponse;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

@Slf4j
@UtilityClass
public class HelperUtil {

    public static Mono<Throwable> handleErrorResponse(ClientResponse clientResponse) {
        return clientResponse.bodyToMono(String.class)
                .flatMap(errorBody -> {
                    log.error("Error response: {}, body: {}", clientResponse.statusCode(), errorBody);
                    return Mono.error(new RuntimeException("Failed to retrieve data: " + errorBody));
                });
    }

    public static MultiValueMap<String, String> getInfoFormData(Integer year, Integer month) {
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("year", String.valueOf(year));
        formData.add("month", String.valueOf(month));
        formData.add("is_global", String.valueOf(0));
        return formData;
    }

    public static MultiValueMap<String, String> sendSmsFormData(String phone, String text,
                                                                String senderName, String callbackUrl) {
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("mobile_phone", phone);
        formData.add("message", text);
        formData.add("from", senderName);
        formData.add("callback_url", callbackUrl);
        return formData;
    }


    public static MultiValueMap<String, String> formDataTemplate(String template) {
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("template", template);
        log.info("Get token request multiValueMap: {}", formData);
        return formData;
    }

    public static MultiValueMap<String, String> getInfoFormData(String dispatchId) {
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("dispatch_id", dispatchId);
        formData.add("count", "0");
        return formData;
    }

    public static boolean isYearAndMonthValid(Integer year, Integer month) {
        return year != null && month != null && month >= 1 && month <= 12 && LocalDate.now().getYear() >= year;
    }
}
