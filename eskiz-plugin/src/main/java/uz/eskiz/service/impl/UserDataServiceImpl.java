package uz.eskiz.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.reactive.function.client.WebClient;
import uz.eskiz.dto.request.CommonResultData;
import uz.eskiz.dto.response.UserDataResponseDTO;
import uz.eskiz.service.UserDataService;
import uz.eskiz.storage.TokenStorage;
import uz.eskiz.util.HelperUtil;

import static uz.eskiz.enums.Constants.AUTHORIZATION_HEADER;
import static uz.eskiz.enums.Constants.BEARER_TOKEN;
import static uz.eskiz.enums.Constants.GET_USER_DATA;

@Slf4j
@RequiredArgsConstructor
public class UserDataServiceImpl implements UserDataService {

    private final WebClient webClient;
    private final TokenStorage tokenStorage;

    @Override
    public CommonResultData<UserDataResponseDTO> getUserData() {
        log.info("## Attempting to retrieve user data with token ##");

        var token = tokenStorage.getToken();
        try {
            return CommonResultData.success(webClient.get()
                    .uri(GET_USER_DATA)
                    .header(AUTHORIZATION_HEADER, BEARER_TOKEN + token)
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, HelperUtil::handleErrorResponse)
                    .bodyToMono(UserDataResponseDTO.class)
                    .block());
        } catch (RuntimeException e) {
            log.error("WebClientException occurred while retrieving user data: {}", e.getMessage());
            return CommonResultData.failed("Failed to retrieve user data: " + e.getMessage());
        }
    }

}
