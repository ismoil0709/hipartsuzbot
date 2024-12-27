package uz.eskiz.storage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import uz.eskiz.config.properties.EskizProperties;
import uz.eskiz.dto.TokenHolderDTO;
import uz.eskiz.dto.request.LoginResponseDTO;

import java.time.LocalDateTime;

import static uz.eskiz.enums.Constants.*;

@Slf4j
@Component(TokenStorage.BEAN)
@RequiredArgsConstructor
public class TokenStorage {

    public static final String BEAN = "token-storage";
    private final WebClient webClient;
    private final EskizProperties properties;

    private TokenHolderDTO holderDTO;

    private LoginResponseDTO.Data login(String email, String password) {
        log.info("## Attempting to retrieve access token ##");
        try {
            return webClient.post()
                    .uri(GET_TOKEN)
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(BodyInserters.fromFormData(tokenFormData(email, password)))
                    .retrieve()
                    .bodyToMono(LoginResponseDTO.class)
                    .map(LoginResponseDTO::getData)
                    .block();
        } catch (RuntimeException e) {
            log.error("Exception occurred while retrieving token: {}", e.getMessage());
            throw new RuntimeException("Exception occurred while retrieving token: " + e.getMessage());
        }
    }

    private LoginResponseDTO.Data refreshToken(String token) {
        log.info("## Attempting to refresh access token ##");
        try {
            return webClient.patch()
                    .uri(REFRESH_TOKEN)
                    .header(AUTHORIZATION_HEADER, BEARER_TOKEN + token)
                    .retrieve()
                    .bodyToMono(LoginResponseDTO.class)
                    .map(LoginResponseDTO::getData)
                    .block();
        } catch (RuntimeException e) {
            log.error("WebClientException occurred while refreshing token: {}", e.getMessage());
            throw new RuntimeException("Failed to refresh token: " + e.getMessage());
        }
    }

    public String getToken() {
        if (holderDTO == null || holderDTO.isTokenExpired()) {
            String newToken = (holderDTO == null)
                    ? login(properties.getEmail(), properties.getPassword()).getToken()
                    : refreshToken(holderDTO.getAccessToken()).getToken();
            saveToken(newToken);
            return newToken;
        }
        return holderDTO.getAccessToken();
    }

    private void saveToken(String newToken) {
        holderDTO = new TokenHolderDTO(newToken,
                LocalDateTime.now().plusDays(29));
    }

    private MultiValueMap<String, String> tokenFormData(String email, String password) {
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("email", email);
        formData.add("password", password);
        return formData;
    }
}
